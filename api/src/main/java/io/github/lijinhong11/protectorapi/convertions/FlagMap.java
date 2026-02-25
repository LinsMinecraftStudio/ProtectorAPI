package io.github.lijinhong11.protectorapi.convertions;

import io.github.lijinhong11.protectorapi.flag.FlagState;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * A map for convert flags fast
 */
public class FlagMap extends HashMap<String, FlagState<?>> {
    public FlagMap() {
        super();
    }

    public FlagMap(int initialCapacity) {
        super(initialCapacity);
    }

    public <T> FlagMap(Map<String, T> map, Function<T, FlagState<?>> converter) {
        super();

        for (Map.Entry<String, T> entry : map.entrySet()) {
            put(entry.getKey(), converter.apply(entry.getValue()));
        }
    }
}
