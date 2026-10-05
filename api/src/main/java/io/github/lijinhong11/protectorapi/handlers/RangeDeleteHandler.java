package io.github.lijinhong11.protectorapi.handlers;

import io.github.lijinhong11.protectorapi.protection.IProtectionModule;
import io.github.lijinhong11.protectorapi.protection.IProtectionRange;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A handler that will be triggered when a protection range is deleted
 */
@FunctionalInterface
public interface RangeDeleteHandler extends AHandler {
    void onDelete(@NotNull IProtectionModule module, @Nullable IProtectionRange range);
}
