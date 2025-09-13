package io.github.lijinhong11.protector.api.flag;

/**
 * Flag register interface.
 * You can register custom flags when the module implements it.
 */
public interface FlagRegisterable {
    /**
     * Register a flag
     *
     * @param flag the flag
     * @throws UnsupportedOperationException if the module doesn't support
     */
    void registerFlag(CustomFlag flag);
}
