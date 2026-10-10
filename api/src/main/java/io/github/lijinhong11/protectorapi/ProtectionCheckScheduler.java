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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Supplier;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

/**
 * Dispatches checks without waiting on the server thread.
 */
final class ProtectionCheckScheduler {
    private final Set<CompletableFuture<?>> pending = new HashSet<>();
    private boolean closed;

    <T> CompletableFuture<T> execute(Function<Request<T>, CompletableFuture<T>> checks) {
        Request<T> request;
        synchronized (pending) {
            if (closed) {
                return CompletableFuture.failedFuture(new CancellationException("ProtectorAPI checks are closed"));
            }
            request = new Request<>();
            pending.add(request);
        }
        request.whenComplete((value, failure) -> {
            synchronized (pending) {
                pending.remove(request);
            }
            CompletableFuture<?> stage = request.current;
            request.current = null;
            if (stage != null) {
                stage.cancel(false);
            }
        });
        try {
            enabledHost();
            if (!request.isDone()) {
                checks.apply(request).whenComplete((value, failure) -> {
                    if (failure == null) {
                        request.complete(value);
                    } else {
                        request.completeExceptionally(failure);
                    }
                });
            }
        } catch (Throwable failure) {
            request.completeExceptionally(failure);
        }
        return request;
    }

    /** Checks are sequential, so a request only needs to retain its current stage. */
    static final class Request<T> extends CompletableFuture<T> {
        private volatile CompletableFuture<?> current;

        <R> CompletableFuture<R> submit(boolean async, Supplier<R> check) {
            CompletableFuture<R> stage = new CompletableFuture<>();
            current = stage;
            if (isDone()) {
                stage.cancel(false);
                return stage;
            }

            try {
                BukkitTask task = dispatch(async, () -> runCheck(stage, check));
                if (task != null) {
                    // Also handles cancellation while dispatch is returning the task handle.
                    stage.whenComplete((value, failure) -> {
                        if (stage.isCancelled()) {
                            task.cancel();
                        }
                    });
                }
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

    private static BukkitTask dispatch(boolean async, Runnable task) {
        JavaPlugin host = enabledHost();
        if (async) {
            return Bukkit.getScheduler().runTaskAsynchronously(host, task);
        } else if (Bukkit.isPrimaryThread()) {
            task.run();
            return null;
        } else {
            return Bukkit.getScheduler().runTask(host, task);
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

    void close() {
        List<CompletableFuture<?>> snapshot;
        synchronized (pending) {
            closed = true;
            snapshot = new ArrayList<>(pending);
        }
        // Complete outside the lock: callbacks may submit stages or close another scheduler.
        snapshot.forEach(
                future -> future.completeExceptionally(new CancellationException("ProtectorAPI checks are closed")));
    }
}
