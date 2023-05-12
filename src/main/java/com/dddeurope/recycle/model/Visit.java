package com.dddeurope.recycle.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Visit {

    private final CityRules rules;

    private final Map<FractionType, Integer> weights = new HashMap<>();

    public Visit(CityRules rules) {
        this.rules = Objects.requireNonNull(rules);
    }

    public void registerDrop(FractionType fraction, int weight) {
        weights.put(fraction, weights.getOrDefault(fraction, 0) + weight);
    }

    public double calculatePrice() {
        double totalPrice = weights.entrySet().stream()
            .mapToDouble(e -> fractionCost(e.getKey(), e.getValue()))
            .sum();

        return Math.round(totalPrice * 100.0) / 100.0;
    }

    private double fractionCost(FractionType fractionType, Integer weight) {
        int exemption = rules.exemptions().getOrDefault(fractionType, 0);
        if (weight > exemption) {
            return rules.priceFor(fractionType) * (weight - exemption);
        }
        return 0;
    }
}
