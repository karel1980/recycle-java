package com.dddeurope.recycle.projections;

import com.dddeurope.recycle.events.IdCardRegistered;

import java.util.HashMap;
import java.util.Map;

public class IdCardRegistrationProjection {
    private final Map<String, IdCardRegistered> cardsById = new HashMap<>();

    public void project(IdCardRegistered event) {
        cardsById.put(event.cardId(), event);
    }

    public String getCity(String cardId) {
        return cardsById.get(cardId).city();
    }
}
