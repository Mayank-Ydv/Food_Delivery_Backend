package com.foodapp.order.statemachine;

/**
 * Events that TRIGGER a state transition.
 * Note: events are verbs (CONFIRM_ORDER), states are nouns (CONFIRMED).
 * This separation is a key LLD concept — don't mix the two.
 */
public enum OrderEvent {
    CONFIRM_ORDER,
    START_PREPARING,
    DISPATCH_ORDER,
    COMPLETE_ORDER,
    CANCEL_ORDER
}
