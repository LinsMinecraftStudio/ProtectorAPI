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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import io.github.lijinhong11.protectorapi.flag.CommonFlags;
import io.github.lijinhong11.protectorapi.flag.FlagStates;
import io.github.lijinhong11.protectorapi.protection.IBlockProtectionModule;
import io.github.lijinhong11.protectorapi.protection.IProtectionModule;
import io.github.lijinhong11.protectorapi.protection.IProtectionRange;
import io.github.lijinhong11.protectorapi.protection.ProtectionCheck;
import java.lang.reflect.Field;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.MockedStatic;

class AsyncProtectionChecksTest {
    private static final JavaPlugin HOST = mock(JavaPlugin.class);
    private final Queue<Runnable> mainTasks = new ArrayDeque<>();
    private final Queue<Runnable> asyncTasks = new ArrayDeque<>();
    private final BukkitScheduler scheduler = mock(BukkitScheduler.class);
    private final World world = mock(World.class);
    private final Player player = mock(Player.class);
    private final Location location = new Location(world, 10, 64, 20);
    private MockedStatic<Bukkit> bukkit;
    private boolean primary = true;

    @BeforeAll
    static void initializeHost() {
        ProtectorAPI.setPluginHost(HOST);
    }

    @BeforeEach
    void setUp() throws Exception {
        clearModules("modules");
        clearModules("blockModules");
        when(HOST.isEnabled()).thenReturn(true);
        when(world.getName()).thenAnswer(invocation -> {
            assertTrue(primary, "Bukkit world access must stay on the server thread");
            return "world";
        });
        when(player.getLocation()).thenAnswer(invocation -> {
            assertTrue(primary, "Player location must be captured on the server thread");
            return location;
        });
        bukkit = mockStatic(Bukkit.class);
        bukkit.when(Bukkit::isPrimaryThread).thenAnswer(invocation -> primary);
        bukkit.when(Bukkit::getScheduler).thenReturn(scheduler);
        when(scheduler.runTask(eq(HOST), any(Runnable.class))).thenAnswer(invocation -> {
            mainTasks.add(invocation.getArgument(1));
            return null;
        });
        when(scheduler.runTaskAsynchronously(eq(HOST), any(Runnable.class))).thenAnswer(invocation -> {
            asyncTasks.add(invocation.getArgument(1));
            return null;
        });
    }

    @AfterEach
    void tearDown() {
        bukkit.close();
    }

    private static void clearModules(String name) throws Exception {
        Field field = ProtectorAPI.class.getDeclaredField(name);
        field.setAccessible(true);
        ((Set<?>) field.get(null)).clear();
    }

    private void drainTasks() {
        int count = 0;
        while (!mainTasks.isEmpty() || !asyncTasks.isEmpty()) {
            assertTrue(count++ < 100, "Unexpected scheduling loop");
            primary = mainTasks.isEmpty();
            if (!mainTasks.isEmpty()) {
                primary = true;
                mainTasks.remove().run();
            } else {
                primary = false;
                asyncTasks.remove().run();
            }
        }
        primary = true;
    }

    @Test
    void capabilitiesDefaultToSynchronous() {
        IProtectionModule region = mock(IProtectionModule.class, CALLS_REAL_METHODS);
        IBlockProtectionModule block = mock(IBlockProtectionModule.class, CALLS_REAL_METHODS);
        for (ProtectionCheck check : ProtectionCheck.values()) {
            assertFalse(region.supportsAsync(check));
            assertFalse(block.supportsAsync(check));
        }
    }

    @Test
    void lookupReturnsWithoutWaitingAndPreservesOrderAndLocation() {
        IProtectionModule first = mock(IProtectionModule.class);
        IProtectionModule second = mock(IProtectionModule.class);
        IProtectionModule third = mock(IProtectionModule.class);
        when(first.supportsAsync(ProtectionCheck.LOOKUP)).thenReturn(true);
        when(first.isInProtectionRange(any(Location.class))).thenAnswer(invocation -> {
            assertFalse(primary);
            Location copy = invocation.getArgument(0);
            assertEquals(10, copy.getBlockX());
            copy.setX(999);
            return false;
        });
        when(second.isInProtectionRange(any(Location.class))).thenAnswer(invocation -> {
            assertTrue(primary);
            assertEquals(10, ((Location) invocation.getArgument(0)).getBlockX());
            return true;
        });
        ProtectorAPI.register(first);
        ProtectorAPI.register(second);
        ProtectorAPI.register(third);

        CompletableFuture<IProtectionModule> result = ProtectorAPI.findModuleAsync(location);
        location.setX(500);
        assertFalse(result.isDone());
        verify(first, never()).isInProtectionRange(any(Location.class));
        drainTasks();

        assertSame(second, result.join());
        verify(third, never()).isInProtectionRange(any(Location.class));
    }

    @ParameterizedTest
    @EnumSource(
            value = CommonFlags.class,
            names = {"BREAK", "PLACE", "INTERACT"})
    void permissionChecksDispatchEachStageAndUseTargetLocation(CommonFlags flag) {
        IProtectionModule region = mock(IProtectionModule.class);
        IProtectionRange range = mock(IProtectionRange.class);
        IBlockProtectionModule block = mock(IBlockProtectionModule.class);
        when(region.supportsAsync(ProtectionCheck.LOOKUP)).thenReturn(true);
        when(region.isInProtectionRange(any(Location.class))).thenAnswer(invocation -> {
            assertFalse(primary);
            assertEquals(10, ((Location) invocation.getArgument(0)).getBlockX());
            return true;
        });
        when(region.getProtectionRangeInfo(any(Location.class))).thenAnswer(invocation -> {
            assertFalse(primary);
            return range;
        });
        doAnswer(invocation -> {
                    assertTrue(primary, "Async lookup must not imply async flags");
                    return FlagStates.ALLOW;
                })
                .when(range)
                .getFlagState(flag, player);
        when(block.isProtected(any(Location.class))).thenAnswer(invocation -> {
            assertTrue(primary);
            return true;
        });
        when(block.supportsAsync(ProtectionCheck.FLAGS)).thenReturn(true);
        when(block.allowBreak(eq(player), any(Location.class))).thenAnswer(invocation -> {
            assertFalse(primary);
            return false;
        });
        when(block.allowPlace(eq(player), any(Location.class))).thenAnswer(invocation -> {
            assertFalse(primary);
            return false;
        });
        when(block.allowInteract(eq(player), any(Location.class))).thenAnswer(invocation -> {
            assertFalse(primary);
            return false;
        });
        ProtectorAPI.register(region);
        ProtectorAPI.register(block);

        CompletableFuture<Boolean> result =
                switch (flag) {
                    case BREAK -> ProtectorAPI.allowBreakAsync(player, location);
                    case PLACE -> ProtectorAPI.allowPlaceAsync(player, location);
                    case INTERACT -> ProtectorAPI.allowInteractAsync(player, location);
                    default -> throw new AssertionError(flag);
                };
        assertFalse(result.isDone());
        drainTasks();
        assertFalse(result.join());
        verify(player, never()).getLocation();
    }

    @Test
    void playerLocationAndGlobalFlagsReturnToMainThread() {
        IProtectionModule region = mock(IProtectionModule.class);
        when(region.supportsAsync(ProtectionCheck.LOOKUP)).thenReturn(true);
        when(region.isSupportGlobalFlags()).thenAnswer(invocation -> {
            assertTrue(primary);
            return true;
        });
        doAnswer(invocation -> {
                    assertTrue(primary);
                    return FlagStates.DENY;
                })
                .when(region)
                .getGlobalFlag(CommonFlags.BREAK, "world");
        ProtectorAPI.register(region);
        primary = false;

        CompletableFuture<Boolean> result = ProtectorAPI.allowBreakAsync(player);
        assertFalse(result.isDone());
        verify(player, never()).getLocation();
        drainTasks();
        assertFalse(result.join());
        verify(player).getLocation();
    }

    @ParameterizedTest
    @EnumSource(
            value = CommonFlags.class,
            names = {"BREAK", "PLACE", "INTERACT"})
    void asyncBlockLookupReturnsToServerThreadForPermissions(CommonFlags flag) {
        IBlockProtectionModule block = mock(IBlockProtectionModule.class);
        when(block.supportsAsync(ProtectionCheck.LOOKUP)).thenReturn(true);
        when(block.isProtected(any(Location.class))).thenAnswer(invocation -> {
            assertFalse(primary, "Verified block lookups should run asynchronously");
            return true;
        });
        when(block.allowBreak(eq(player), any(Location.class))).thenAnswer(invocation -> {
            assertTrue(primary, "Lookup support must not enable async permission checks");
            return false;
        });
        when(block.allowPlace(eq(player), any(Location.class))).thenAnswer(invocation -> {
            assertTrue(primary, "Lookup support must not enable async permission checks");
            return false;
        });
        when(block.allowInteract(eq(player), any(Location.class))).thenAnswer(invocation -> {
            assertTrue(primary, "Lookup support must not enable async permission checks");
            return false;
        });
        ProtectorAPI.register(block);

        CompletableFuture<Boolean> result =
                switch (flag) {
                    case BREAK -> ProtectorAPI.allowBreakAsync(player, location);
                    case PLACE -> ProtectorAPI.allowPlaceAsync(player, location);
                    case INTERACT -> ProtectorAPI.allowInteractAsync(player, location);
                    default -> throw new AssertionError(flag);
                };
        assertFalse(result.isDone());
        drainTasks();
        assertFalse(result.join());
    }

    @Test
    void regionDenialShortCircuitsBlockChecks() {
        IProtectionModule region = mock(IProtectionModule.class);
        IProtectionRange range = mock(IProtectionRange.class);
        IBlockProtectionModule block = mock(IBlockProtectionModule.class);
        when(region.isInProtectionRange(any(Location.class))).thenReturn(true);
        when(region.getProtectionRangeInfo(any(Location.class))).thenReturn(range);
        doReturn(FlagStates.DENY).when(range).getFlagState(CommonFlags.PLACE, player);
        ProtectorAPI.register(region);
        ProtectorAPI.register(block);

        assertFalse(ProtectorAPI.allowPlaceAsync(player, location).join());
        verify(block, never()).isProtected(any(Location.class));
    }

    @Test
    void unsupportedFlagAllowsAndLookupFallsBackToBlockModules() {
        IProtectionModule region = mock(IProtectionModule.class);
        IProtectionRange range = mock(IProtectionRange.class);
        when(region.isInProtectionRange(any(Location.class))).thenReturn(true);
        when(region.getProtectionRangeInfo(any(Location.class))).thenReturn(range);
        doReturn(FlagStates.UNSUPPORTED).when(range).getFlagState(CommonFlags.INTERACT, player);
        ProtectorAPI.register(region);
        assertTrue(ProtectorAPI.allowInteractAsync(player, location).join());

        when(region.isInProtectionRange(any(Location.class))).thenReturn(false);
        IBlockProtectionModule block = mock(IBlockProtectionModule.class);
        when(block.supportsAsync(ProtectionCheck.LOOKUP)).thenReturn(true);
        when(block.isProtected(any(Location.class))).thenReturn(true);
        ProtectorAPI.register(block);
        CompletableFuture<Boolean> result = ProtectorAPI.isInProtectionRangeAsync(location);
        assertFalse(result.isDone());
        drainTasks();
        assertTrue(result.join());
    }

    @Test
    void moduleFailureIsExceptionalAndNeverBecomesAnAllow() {
        IProtectionModule region = mock(IProtectionModule.class);
        IllegalStateException failure = new IllegalStateException("plugin check failed");
        when(region.supportsAsync(ProtectionCheck.LOOKUP)).thenReturn(true);
        when(region.isInProtectionRange(any(Location.class))).thenThrow(failure);
        ProtectorAPI.register(region);

        CompletableFuture<Boolean> result = ProtectorAPI.allowBreakAsync(player, location);
        drainTasks();
        assertSame(
                failure, assertThrows(CompletionException.class, result::join).getCause());
    }

    @Test
    void rejectedTasksAndDisableCompletePendingFutures() {
        ProtectionCheckScheduler dispatcher = new ProtectionCheckScheduler();
        Runnable check = mock(Runnable.class);
        CompletableFuture<Boolean> pending = dispatcher.submit(true, () -> {
            check.run();
            return true;
        });
        dispatcher.close();
        assertTrue(pending.isCancelled());
        assertTrue(dispatcher.submit(true, () -> true).isCancelled());
        drainTasks();
        verifyNoInteractions(check);

        IllegalStateException rejected = new IllegalStateException("scheduler stopped");
        when(scheduler.runTaskAsynchronously(eq(HOST), any(Runnable.class))).thenThrow(rejected);
        CompletableFuture<Boolean> result = new ProtectionCheckScheduler().submit(true, () -> true);
        assertSame(
                rejected, assertThrows(CompletionException.class, result::join).getCause());
    }

    @Test
    void noModulesAllowsWithoutScheduling() {
        assertTrue(ProtectorAPI.allowBreakAsync(player, location).join());
        assertFalse(ProtectorAPI.isInProtectionRangeAsync(location).join());
        verifyNoInteractions(scheduler);
    }
}
