package io.github.lijinhong11.protectorapi.handlers;

import io.github.lijinhong11.protectorapi.protection.IProtectionModule;
import io.github.lijinhong11.protectorapi.protection.IProtectionRange;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface RangeCreateHandler extends AHandler {
    void onCreate(@NotNull IProtectionModule module, @NotNull IProtectionRange range);
}
