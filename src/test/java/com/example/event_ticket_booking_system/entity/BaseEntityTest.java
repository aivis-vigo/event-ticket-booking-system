package com.example.event_ticket_booking_system.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("BaseEntity Tests")
class BaseEntityTest {

    /*
     * BaseEntity is abstract, so we test it through Ticket which extends it.
     */

    @Test
    @DisplayName("Should start with null id")
    void testDefaultIdIsNull() {
        Ticket ticket = new Ticket();
        assertNull(ticket.getId());
    }

    @Test
    @DisplayName("Should set and get id")
    void testSetAndGetId() {
        Ticket ticket = new Ticket();
        ticket.setId(10L);
        assertEquals(10L, ticket.getId());
    }

    @Test
    @DisplayName("Should allow overwriting id")
    void testOverwriteId() {
        Ticket ticket = new Ticket();
        ticket.setId(1L);
        ticket.setId(99L);
        assertEquals(99L, ticket.getId());
    }

    @Test
    @DisplayName("Should allow setting id to null")
    void testSetIdToNull() {
        Ticket ticket = new Ticket();
        ticket.setId(5L);
        ticket.setId(null);
        assertNull(ticket.getId());
    }

    @Test
    @DisplayName("Subclass should inherit id field from BaseEntity")
    void testSubclassInheritsIdField() {
        Ticket ticket = new Ticket();
        ticket.setId(42L);
        // getId() comes from BaseEntity
        assertEquals(42L, ticket.getId());
    }
}