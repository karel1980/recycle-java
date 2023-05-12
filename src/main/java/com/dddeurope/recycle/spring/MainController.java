package com.dddeurope.recycle.spring;

import com.dddeurope.recycle.aggregates.Visit;
import com.dddeurope.recycle.aggregates.Visits;
import com.dddeurope.recycle.commands.CommandMessage;
import com.dddeurope.recycle.events.Event;
import com.dddeurope.recycle.events.EventMessage;
import com.dddeurope.recycle.events.FractionWasDropped;
import com.dddeurope.recycle.events.IdCardRegistered;
import com.dddeurope.recycle.events.PriceWasCalculated;
import com.dddeurope.recycle.projections.IdCardRegistrationProjection;
import com.dddeurope.recycle.projections.PriceProjection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class MainController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MainController.class);

    @GetMapping("/validate")
    public String validate() {
        return "Hi!";
    }

    @PostMapping("/handle-command")
    public ResponseEntity<EventMessage> handle(@RequestBody RecycleRequest request) {
        LOGGER.info("Incoming Request: {}", request.asString());

        EventMessage message = handleRequest(request);
        return ResponseEntity.ok(message);
    }

    EventMessage handleRequest(RecycleRequest request) {
        IdCardRegistrationProjection idCardRegistrationProjection = new IdCardRegistrationProjection();
        List<IdCardRegistered> registrations = request.getEventsOfType(IdCardRegistered.class);
        registrations.forEach(idCardRegistrationProjection::project);

        PriceProjection priceProjection = new PriceProjection(idCardRegistrationProjection);
        List<FractionWasDropped> drops = request.getEventsOfType(FractionWasDropped.class);
        drops.forEach(priceProjection::project);

        String cardId = registrations.get(0).cardId();
        return new EventMessage("todo", new PriceWasCalculated(cardId, priceProjection.getPrice(), "EUR"));
    }

    public record RecycleRequest(List<EventMessage> history, CommandMessage command) {

        public <T extends Event> List<T> getEventsOfType(Class<T> type) {
            return this.history().stream()
                .map(EventMessage::getPayload)
                .filter(m -> type.isAssignableFrom(m.getClass()))
                .map(type::cast)
                .toList();
        }

        public String asString() {
            var historyAsString = history.stream()
                .map(EventMessage::toString)
                .collect(Collectors.joining("\n\t"));

            return String.format("%n%s %nWith History\n\t%s", command, historyAsString);
        }
    }
}
