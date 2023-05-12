package com.dddeurope.recycle.model;

import static com.dddeurope.recycle.model.FractionType.CONSTRUCTION_WASTE;
import static com.dddeurope.recycle.model.FractionType.GREEN_WASTE;
import static com.dddeurope.recycle.model.FractionType.byName;

import com.dddeurope.recycle.events.Event;
import com.dddeurope.recycle.events.FractionWasDropped;
import com.dddeurope.recycle.events.IdCardScannedAtEntranceGate;
import com.dddeurope.recycle.events.IdCardScannedAtExitGate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Customer {

    private final CityRules DEFAULT_CITY_RULES = new CityRules(
        Map.of(
            CONSTRUCTION_WASTE, 0.15,
            GREEN_WASTE, 0.09
        ),
        Map.of(
            CONSTRUCTION_WASTE, 0,
            GREEN_WASTE, 0
        ));

    private Map<String, CityRules> cityRules = Map.of(
        "South Park",
        new CityRules(
            Map.of(
                CONSTRUCTION_WASTE, 0.18,
                GREEN_WASTE, 0.12
            ),
            Map.of(
                CONSTRUCTION_WASTE, 100,
                GREEN_WASTE, 50
            )
        )
    );

    private final String city;
    private final List<Visit> visits = new ArrayList<>();

    public Customer(String city) {
        this.city = city;
    }

    public void handle(Event event) {
        if (event instanceof IdCardScannedAtEntranceGate) {
            visits.add(new Visit(cityRules.getOrDefault(city, DEFAULT_CITY_RULES)));
        } else if (event instanceof FractionWasDropped drop) {
            mostRecentVisit().registerDrop(byName(drop.fractionType()), drop.weight());
        } else if (event instanceof IdCardScannedAtExitGate) {
            //            mostRecentVisit().close(priceTable);
        }
    }

    private Visit mostRecentVisit() {
        return visits.get(visits.size() - 1);
    }

    public double calculatePriceOfMostRecentVisit() {
        return mostRecentVisit().calculatePrice();
    }
}
