package io.github.lijinhong11.protector.api.flag;

import org.jetbrains.annotations.Nullable;

public enum FlagState {
    /**
     * Marked as the flag is allowed.
     */
    ALLOW,
    /**
     * Marked as the flag is not set.
     */
    NOT_SET,
    /**
     * Marked as the flag is denied.
     */
    DENY,
    /**
     * Marked as the flag is not exists or not supported in the corresponding plugin.
     */
    NULL;

    public static FlagState fromBoolean(boolean flag) {
        return flag ? ALLOW : DENY;
    }

    public static FlagState fromNullableBoolean(@Nullable Boolean flag) {
        return flag == null ? NULL : fromBoolean(flag);
    }
}
