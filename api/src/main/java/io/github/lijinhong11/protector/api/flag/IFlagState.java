package io.github.lijinhong11.protector.api.flag;

public interface IFlagState<T> {
    T value();

    default boolean isBooleanValue() {
        return value() instanceof Boolean;
    }

    default boolean toBooleanIfPossible() {
        if (isBooleanValue()) {
            return (Boolean) value();
        }

        throw new RuntimeException(new ClassCastException());
    }
}
