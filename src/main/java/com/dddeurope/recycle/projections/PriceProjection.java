package com.dddeurope.recycle.projections;

import com.dddeurope.recycle.events.FractionWasDropped;

import java.util.Map;

public class PriceProjection {

    private Map<String, Double> pricePerKgFractionType = Map.of(
        "Construction waste", 0.15,
        "Green waste", 0.09
    );
    double price = 0.0;

    public void project(FractionWasDropped dropped) {
        price += dropped.weight() * pricePerKgFractionType.get(dropped.fractionType());
    }

    public double getPrice() {
        return Math.round(price * 100) / 100.0;
    }
}
