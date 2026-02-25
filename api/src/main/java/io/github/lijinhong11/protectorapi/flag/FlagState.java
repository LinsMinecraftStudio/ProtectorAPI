package io.github.lijinhong11.protectorapi.flag;

public interface FlagState<T> {
    T value();

    default boolean isBooleanValue() {
        return value() instanceof Boolean;
    }

    /**
     * Converts the value to boolean.
     * @return the boolean value
     * @throws ClassCastException if failed to convert
     */
    default boolean toBooleanOrThrow() {
        if (isBooleanValue()) {
            return (Boolean) value();
        }

        throw new RuntimeException(new ClassCastException());
    }
}
