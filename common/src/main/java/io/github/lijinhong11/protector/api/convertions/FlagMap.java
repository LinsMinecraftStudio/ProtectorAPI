package io.github.lijinhong11.protector.api.convertions;

import io.github.lijinhong11.protector.api.flag.IFlagState;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class FlagMap extends HashMap<String, IFlagState<?>> {
    public FlagMap() {
        super();
    }

    public <T> FlagMap(Map<String, T> map, Function<T, IFlagState<?>> converter) {
        super();
        for (Map.Entry<String, T> entry : map.entrySet()) {
            put(entry.getKey(), converter.apply(entry.getValue()));
        }
    }
}
