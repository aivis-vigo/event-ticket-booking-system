package com.example.event_ticket_booking_system.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Event Entity Tests")
class EventTest {

    private Event event;
    private LocalDateTime eventDate;

    @BeforeEach
    void setUp() {
        eventDate = LocalDateTime.of(2026, 6, 15, 19, 0);
        event = new Event("Summer Music Festival", "An amazing music festival", eventDate,
                "Central Park", 1000, 99.99);
    }

    @Test
    @DisplayName("Should create an event with all fields")
    void testEventConstruction() {
        assertNotNull(event);
        assertEquals("Summer Music Festival", event.getName());
        assertEquals("An amazing music festival", event.getDescription());
        assertEquals(eventDate, event.getEventDate());
        assertEquals("Central Park", event.getLocation());
        assertEquals(1000, event.getTotalTickets());
        assertEquals(99.99, event.getTicketPrice());
    }

    @Test
    @DisplayName("Should create an empty event")
    void testEmptyEventConstruction() {
        Event emptyEvent = new Event();
        assertNotNull(emptyEvent);
        assertNull(emptyEvent.getId());
        assertNull(emptyEvent.getName());
    }

    @Test
    @DisplayName("Should set and get event ID")
    void testSetAndGetId() {
        event.setId(1L);
        assertEquals(1L, event.getId());
    }

    @Test
    @DisplayName("Should set and get event name")
    void testSetAndGetName() {
        event.setName("Winter Festival");
        assertEquals("Winter Festival", event.getName());
    }

    @Test
    @DisplayName("Should set and get event description")
    void testSetAndGetDescription() {
        event.setDescription("A festival in winter");
        assertEquals("A festival in winter", event.getDescription());
    }

    @Test
    @DisplayName("Should set and get event date")
    void testSetAndGetEventDate() {
        LocalDateTime newDate = LocalDateTime.of(2026, 12, 25, 18, 0);
        event.setEventDate(newDate);
        assertEquals(newDate, event.getEventDate());
    }

    @Test
    @DisplayName("Should set and get event location")
    void testSetAndGetLocation() {
        event.setLocation("Madison Square Garden");
        assertEquals("Madison Square Garden", event.getLocation());
    }

    @Test
    @DisplayName("Should set and get total tickets")
    void testSetAndGetTotalTickets() {
        event.setTotalTickets(5000);
        assertEquals(5000, event.getTotalTickets());
    }

    @Test
    @DisplayName("Should set and get ticket price")
    void testSetAndGetTicketPrice() {
        event.setTicketPrice(149.99);
        assertEquals(149.99, event.getTicketPrice());
    }

    @Test
    @DisplayName("Should handle zero tickets")
    void testZeroTickets() {
        event.setTotalTickets(0);
        assertEquals(0, event.getTotalTickets());
    }

    @Test
    @DisplayName("Should handle high ticket price")
    void testHighTicketPrice() {
        event.setTicketPrice(999.99);
        assertEquals(999.99, event.getTicketPrice());
    }
}

