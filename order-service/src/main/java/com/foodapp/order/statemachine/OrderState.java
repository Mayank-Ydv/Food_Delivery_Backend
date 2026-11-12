package com.foodapp.order.statemachine;

/**
 * The 6 states an order can be in.
 * This is the EXACT thing interviewers ask about: "Walk me through your
 * order lifecycle design." Be ready to draw this on a whiteboard.
 */
public enum OrderState {
    PLACED,
    CONFIRMED,
    PREPARING,
    OUT_FOR_DELIVERY,
    DELIVERED,
    CANCELLED
}
