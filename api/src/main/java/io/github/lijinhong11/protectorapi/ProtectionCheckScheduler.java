/*
 * ProtectorAPI
 * Copyright (C) 2026 lijinhong11(mmmjjkx)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package io.github.lijinhong11.protectorapi;

import java.util.Set;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Dispatches checks without waiting on the server thread.
 */
final class ProtectionCheckScheduler {
    private final Set<CompletableFuture<?>> pending = ConcurrentHashMap.newKeySet();
    private volatile boolean closed;

    <T> CompletableFuture<T> submit(boolean async, Supplier<T> check) {
        CompletableFuture<T> result = new CompletableFuture<>();
        pending.add(result);
        result.whenComplete((value, failure) -> pending.remove(result));
        if (closed) {
            result.completeExceptionally(new CancellationException("ProtectorAPI is disabled"));
            return result;
        }

        try {
            dispatch(async, () -> complete(result, check));
        } catch (Throwable failure) {
            result.completeExceptionally(failure);
        }
        return result;
    }

    private void dispatch(boolean async, Runnable task) {
        JavaPlugin host = ProtectorAPI.getPluginHost();
        if (host == null || !host.isEnabled()) {
            throw new IllegalStateException("ProtectorAPI must be enabled before scheduling checks");
        }
        if (async) {
            Bukkit.getScheduler().runTaskAsynchronously(host, task);
        } else if (Bukkit.isPrimaryThread()) {
            task.run();
        } else {
            Bukkit.getScheduler().runTask(host, task);
        }
    }

    private static <T> void complete(CompletableFuture<T> result, Supplier<T> check) {
        if (result.isDone()) {
            return;
        }
        try {
            result.complete(check.get());
        } catch (Throwable failure) {
            result.completeExceptionally(failure);
        }
    }

    void close() {
        closed = true;
        pending.forEach(future -> future.completeExceptionally(new CancellationException("ProtectorAPI is disabled")));
    }
}
