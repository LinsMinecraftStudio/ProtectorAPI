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

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Supplier;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Dispatches checks without waiting on the server thread.
 */
final class ProtectionCheckScheduler {
    <T> CompletableFuture<T> execute(Function<Request<T>, CompletableFuture<T>> checks) {
        Request<T> request = new Request<>();
        try {
            enabledHost();
            checks.apply(request).whenComplete((value, failure) -> {
                if (failure == null) {
                    request.complete(value);
                } else {
                    request.completeExceptionally(failure);
                }
            });
        } catch (Throwable failure) {
            request.completeExceptionally(failure);
        }
        return request;
    }

    /** Executes one stage and lets the composed future schedule the next stage. */
    static final class Request<T> extends CompletableFuture<T> {
        <R> CompletableFuture<R> submit(boolean async, Supplier<R> check) {
            CompletableFuture<R> stage = new CompletableFuture<>();
            try {
                dispatch(async, () -> runCheck(stage, check));
            } catch (Throwable failure) {
                stage.completeExceptionally(failure);
            }
            return stage;
        }
    }

    private static JavaPlugin enabledHost() {
        JavaPlugin host = ProtectorAPI.getPluginHost();
        if (host == null || !host.isEnabled()) {
            throw new IllegalStateException("ProtectorAPI must be enabled before scheduling checks");
        }
        return host;
    }

    private static void dispatch(boolean async, Runnable task) {
        JavaPlugin host = enabledHost();
        if (async) {
            Bukkit.getScheduler().runTaskAsynchronously(host, task);
        } else if (Bukkit.isPrimaryThread()) {
            task.run();
        } else {
            Bukkit.getScheduler().runTask(host, task);
        }
    }

    private static <T> void runCheck(CompletableFuture<T> result, Supplier<T> check) {
        if (result.isDone()) {
            return;
        }
        try {
            result.complete(check.get());
        } catch (Throwable failure) {
            result.completeExceptionally(failure);
        }
    }
}
