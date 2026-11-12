package com.foodapp.order.statemachine;

import com.foodapp.order.exception.InvalidStateTransitionException;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

/**
 * THIS IS THE INTERVIEW CENTERPIECE OF THE WHOLE PROJECT.
 *
 * Instead of scattering "if order.status == X" checks across the codebase,
 * we centralize all valid transitions in ONE map. This is a textbook
 * State Machine / State pattern implementation.
 *
 * Why this matters in an interview: it shows you think about invalid
 * states BEFORE they happen, not after a bug report. You literally cannot
 * skip from PLACED to DELIVERED — the map won't allow it.
 */
@Component
public class OrderStateMachine {

    // Defines: from this state, which events are allowed, and what state do they lead to?
    private static final Map<OrderState, Map<OrderEvent, OrderState>> TRANSITIONS = new EnumMap<>(OrderState.class);

    static {
        TRANSITIONS.put(OrderState.PLACED, Map.of(
                OrderEvent.CONFIRM_ORDER, OrderState.CONFIRMED,
                OrderEvent.CANCEL_ORDER, OrderState.CANCELLED
        ));

        TRANSITIONS.put(OrderState.CONFIRMED, Map.of(
                OrderEvent.START_PREPARING, OrderState.PREPARING,
                OrderEvent.CANCEL_ORDER, OrderState.CANCELLED
        ));

        TRANSITIONS.put(OrderState.PREPARING, Map.of(
                OrderEvent.DISPATCH_ORDER, OrderState.OUT_FOR_DELIVERY
        ));

        TRANSITIONS.put(OrderState.OUT_FOR_DELIVERY, Map.of(
                OrderEvent.COMPLETE_ORDER, OrderState.DELIVERED
        ));

        // Terminal states — no events allowed out of these
        TRANSITIONS.put(OrderState.DELIVERED, Map.of());
        TRANSITIONS.put(OrderState.CANCELLED, Map.of());
    }

    /**
     * Attempts to transition. Throws if the transition isn't allowed.
     * Example: trying CANCEL_ORDER on an OUT_FOR_DELIVERY order throws —
     * you can't cancel food that's already left the restaurant.
     */
    public OrderState transition(OrderState currentState, OrderEvent event) {
        Map<OrderEvent, OrderState> allowedTransitions = TRANSITIONS.get(currentState);

        if (allowedTransitions == null || !allowedTransitions.containsKey(event)) {
            throw new InvalidStateTransitionException(
                    String.format("Cannot apply event '%s' to order in state '%s'", event, currentState)
            );
        }

        return allowedTransitions.get(event);
    }

    public Set<OrderEvent> getAvailableEvents(OrderState currentState) {
        Map<OrderEvent, OrderState> transitions = TRANSITIONS.get(currentState);
        return transitions == null ? EnumSet.noneOf(OrderEvent.class) : transitions.keySet();
    }
}
