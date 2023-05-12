package com.dddeurope.recycle.projections;

import com.dddeurope.recycle.events.FractionWasDropped;

import java.util.Map;

public class PriceProjection {
    private final CityRules DEFAULT_CITY_RULES = new CityRules(
        Map.of(
            "Construction waste", 0.15,
            "Green waste", 0.09
        ),
        Map.of(
            "Construction waste", 0,
            "Green waste", 0
        ));

    private Map<String, CityRules> cityRules = Map.of(
        "South Park",
        new CityRules(
            Map.of(
                "Construction waste", 0.18,
                "Green waste", 0.12
            ),
            Map.of(
                "Construction waste", 100,
                "Green waste", 50
            )
        )
    );

    private double price = 0.0;

    private final IdCardRegistrationProjection idCardRegistrationProjection;

    public PriceProjection(IdCardRegistrationProjection idCardRegistrationProjection) {
        this.idCardRegistrationProjection = idCardRegistrationProjection;
    }

    public void project(FractionWasDropped dropped) {
        String cardId = dropped.cardId();
        String city = idCardRegistrationProjection.getCity(cardId);
        CityRules cityRules = this.cityRules.getOrDefault(city, DEFAULT_CITY_RULES);
        double pricePerKg = cityRules.priceFor(dropped.fractionType());
        int exemption = cityRules.exemptions.getOrDefault(dropped.fractionType(), 0);

        if (dropped.weight() > exemption) {
            price += (dropped.weight() - exemption) * pricePerKg;
        }
    }

    public double getPrice() {
        return Math.round(price * 100) / 100.0;
    }

    private record CityRules(
        Map<String, Double> pricesPerKg,
        Map<String, Integer> exemptions
    ) {
        Double priceFor(String fractionType) {
            return pricesPerKg.get(fractionType);
        }
    }
}
