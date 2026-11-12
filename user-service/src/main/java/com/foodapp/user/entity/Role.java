package com.foodapp.user.entity;

/**
 * Three roles in the system. Used for role-based access control (RBAC).
 * CUSTOMER places orders, DELIVERY_AGENT delivers them, ADMIN manages everything.
 */
public enum Role {
    CUSTOMER,
    DELIVERY_AGENT,
    ADMIN
}
