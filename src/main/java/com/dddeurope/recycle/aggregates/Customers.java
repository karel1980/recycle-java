package com.dddeurope.recycle.aggregates;

import com.dddeurope.recycle.events.Event;
import com.dddeurope.recycle.events.IdCardRegistered;
import com.dddeurope.recycle.model.CardId;
import com.dddeurope.recycle.model.Customer;

import java.util.HashMap;
import java.util.Map;

public class Customers {

    private final Map<CardId, Customer> customers = new HashMap<>();

    public void handle(Event event) {
        if (event instanceof IdCardRegistered reg) {
            addCustomer(reg);
        } else {
            customers.get(new CardId(event.cardId())).handle(event);
        }
    }

    private void addCustomer(IdCardRegistered reg) {
        customers.put(new CardId(reg.cardId()), createCustomer(reg));
    }

    private Customer createCustomer(IdCardRegistered reg) {
        return new Customer(reg.city());
    }

    public Customer getCustomer(CardId cardId) {
        return customers.get(cardId);
    }
}
