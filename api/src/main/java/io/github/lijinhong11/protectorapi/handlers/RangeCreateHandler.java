package io.github.lijinhong11.protectorapi.handlers;

import io.github.lijinhong11.protectorapi.protection.IProtectionModule;
import io.github.lijinhong11.protectorapi.protection.IProtectionRange;
import org.jetbrains.annotations.NotNull;

/**
 * A handler that will be triggered when a protection range is created
 */
@FunctionalInterface
public interface RangeCreateHandler extends AHandler {
    void onCreate(@NotNull IProtectionModule module, @NotNull IProtectionRange range);
}
