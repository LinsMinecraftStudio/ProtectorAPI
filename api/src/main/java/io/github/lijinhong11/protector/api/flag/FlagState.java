package io.github.lijinhong11.protector.api.flag;

import java.util.Objects;
import org.jetbrains.annotations.Nullable;

public class FlagState {
    public static final IFlagState<Boolean> ALLOW = new SimpleFlagState<>(FlagType.BOOLEAN, true);
    public static final IFlagState<Boolean> DENY = new SimpleFlagState<>(FlagType.BOOLEAN, false);
    public static final IFlagState<Boolean> NOT_SET = new SimpleFlagState<>(FlagType.BOOLEAN, null);
    public static final IFlagState<Object> UNSUPPORTED = new UnsupportedFlagState();
    public static final IFlagState<Object> WORLD_NOT_FOUND = new WorldNotFoundFlagState();

    private FlagState() {}

    public static IFlagState<Boolean> fromBoolean(boolean value) {
        return value ? ALLOW : DENY;
    }

    public static IFlagState<Boolean> fromNullableBoolean(@Nullable Boolean value) {
        if (value == null) return NOT_SET;
        return fromBoolean(value);
    }

    public static <T> IFlagState<T> of(FlagType<T> type, @Nullable T value) {
        return new SimpleFlagState<>(type, value);
    }

    static class SimpleFlagState<T> implements IFlagState<T> {
        private final FlagType<T> type;
        private final T value;

        SimpleFlagState(FlagType<T> type, T value) {
            this.type = Objects.requireNonNull(type, "FlagType cannot be null");
            this.value = value;
        }

        @Override
        public FlagType<T> getFlagType() {
            return type;
        }

        @Override
        public T getValue() {
            return value;
        }
    }

    static class UnsupportedFlagState implements IFlagState<Object> {
        UnsupportedFlagState() {}

        @Override
        public FlagType<Object> getFlagType() {
            throw new UnsupportedOperationException();
        }

        public Object getValue() {
            throw new UnsupportedOperationException();
        }
    }

    static class WorldNotFoundFlagState implements IFlagState<Object> {
        WorldNotFoundFlagState() {}

        @Override
        public FlagType<Object> getFlagType() {
            throw new UnsupportedOperationException();
        }

        public Object getValue() {
            throw new UnsupportedOperationException();
        }
    }

    public static final class FlagType<T> {
        public static final FlagType<Boolean> BOOLEAN = of(Boolean.class);
        public static final FlagType<Object> OBJECT = of(Object.class);

        private final Class<T> typeClass;

        private FlagType(Class<T> typeClass) {
            this.typeClass = Objects.requireNonNull(typeClass, "Class cannot be null");
        }

        private static <T> FlagType<T> of(Class<T> clazz) {
            return new FlagType<>(clazz);
        }

        @Override
        public String toString() {
            return "FlagType<" + typeClass.getSimpleName() + ">";
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (!(obj instanceof FlagType<?> other)) return false;
            return Objects.equals(typeClass, other.typeClass);
        }

        @Override
        public int hashCode() {
            return Objects.hash(typeClass);
        }
    }
}
