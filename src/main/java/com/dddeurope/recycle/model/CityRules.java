package com.dddeurope.recycle.model;

import java.util.Map;

public record CityRules(
    Map<FractionType, Double> pricesPerKg,
    Map<FractionType, Integer> exemptions
) {
    public Double priceFor(FractionType fractionType) {
        return pricesPerKg.get(fractionType);
    }
}
