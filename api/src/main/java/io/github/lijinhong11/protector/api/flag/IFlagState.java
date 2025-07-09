package io.github.lijinhong11.protector.api.flag;

public interface IFlagState<T> {
    FlagState.FlagType<T> getFlagType();

    T getValue();
}
