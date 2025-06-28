package io.github.lijinhong11.protector.api.convertions;

import io.github.lijinhong11.protector.api.flag.FlagState;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class FlagMap extends HashMap<String, FlagState> {
    public FlagMap() {
        super();
    }

    public FlagMap(Map<String, FlagState> map) {
        super(map);
    }

    public <T> FlagMap(Map<String, T> map, Function<T, FlagState> converter) {
        super();
        for (Map.Entry<String, T> entry : map.entrySet()) {
            put(entry.getKey(), converter.apply(entry.getValue()));
        }
    }
}
