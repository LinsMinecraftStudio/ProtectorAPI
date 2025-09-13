package io.github.lijinhong11.protector.api.flag;

import org.jetbrains.annotations.Nullable;

public class FlagState {
    public static final IFlagState<Boolean> ALLOW = new SimpleFlagState<>(true);
    public static final IFlagState<Boolean> DENY = new SimpleFlagState<>(false);
    public static final IFlagState<Boolean> NOT_SET = new SimpleFlagState<>(null);
    /**
     * Will use this flag state when the flag is not supported by the protection module.
     */
    public static final IFlagState<Object> UNSUPPORTED = new UnsupportedFlagState();
    /**
     * Will use this flag state when the world is not found.
     */
    public static final IFlagState<Object> WORLD_NOT_FOUND = new WorldNotFoundFlagState();

    private FlagState() {
    }

    public static IFlagState<Boolean> fromBoolean(boolean value) {
        return value ? ALLOW : DENY;
    }

    public static IFlagState<Boolean> fromNullableBoolean(@Nullable Boolean value) {
        if (value == null) return NOT_SET;
        return fromBoolean(value);
    }

    public static <T> IFlagState<T> of(@Nullable T value) {
        return new SimpleFlagState<>(value);
    }

    record SimpleFlagState<T>(T value) implements IFlagState<T> {
    }

    static class UnsupportedFlagState implements IFlagState<Object> {
        UnsupportedFlagState() {
        }

        public Object value() {
            throw new UnsupportedOperationException();
        }
    }

    static class WorldNotFoundFlagState implements IFlagState<Object> {
        WorldNotFoundFlagState() {
        }

        public Object value() {
            throw new UnsupportedOperationException();
        }
    }
}
