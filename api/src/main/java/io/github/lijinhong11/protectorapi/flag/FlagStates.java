package io.github.lijinhong11.protectorapi.flag;

import org.jetbrains.annotations.Nullable;

public class FlagStates {
    public static final FlagState<Boolean> ALLOW = new SimpleFlagState<>(true);
    public static final FlagState<Boolean> DENY = new SimpleFlagState<>(false);
    public static final FlagState<Boolean> NOT_SET = new SimpleFlagState<>(null);
    /**
     * Will use this flag state when the flag is not supported by the protection module.
     */
    public static final FlagState<Object> UNSUPPORTED = new UnsupportedFlagState();
    /**
     * Will use this flag state when the world is not found.
     */
    public static final FlagState<Object> WORLD_NOT_FOUND = new WorldNotFoundFlagState();

    private FlagStates() {}

    public static FlagState<Boolean> fromBoolean(boolean value) {
        return value ? ALLOW : DENY;
    }

    public static FlagState<Boolean> fromNullableBoolean(@Nullable Boolean value) {
        if (value == null) return NOT_SET;
        return fromBoolean(value);
    }

    public static <T> FlagState<T> of(@Nullable T value) {
        return new SimpleFlagState<>(value);
    }

    record SimpleFlagState<T>(T value) implements FlagState<T> {}

    public static class UnsupportedFlagState implements FlagState<Object> {
        UnsupportedFlagState() {}

        public Object value() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isBooleanValue() {
            return false;
        }
    }

    public static class WorldNotFoundFlagState extends UnsupportedFlagState {
        WorldNotFoundFlagState() {}
    }
}
