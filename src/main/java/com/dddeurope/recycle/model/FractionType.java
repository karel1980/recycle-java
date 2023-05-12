package com.dddeurope.recycle.model;

import java.util.Arrays;

public enum FractionType {
    CONSTRUCTION_WASTE("Construction waste"),
    GREEN_WASTE("Green waste");

    private String name;

    FractionType(String name) {
        this.name = name;
    }

    static FractionType byName(String name) {
        return Arrays.stream(values())
            .filter(f -> f.name.equals(name))
            .findAny()
            .orElseThrow();
    }
}
