package io.github.lijinhong11.protectorapi.handlers;

import io.github.lijinhong11.protectorapi.protection.IProtectionModule;
import io.github.lijinhong11.protectorapi.protection.IProtectionRange;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface RangeDeleteHandler extends AHandler {
    void onDelete(@NotNull IProtectionModule module, @Nullable IProtectionRange range);
}
