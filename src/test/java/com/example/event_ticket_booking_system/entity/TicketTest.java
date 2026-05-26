package com.example.event_ticket_booking_system.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Ticket Entity Tests")
class TicketTest {

    private Ticket ticket;
    private Event event;

    @BeforeEach
    void setUp() {
        LocalDateTime eventDate = LocalDateTime.of(2026, 6, 15, 19, 0);
        event = new Event("Summer Music Festival", "An amazing music festival", eventDate,
                "Central Park", 1000, 99.99);
        ticket = new Ticket(event, "TICKET-001", 99.99);
    }

    @Test
    @DisplayName("Should create a ticket with all fields")
    void testTicketConstruction() {
        assertNotNull(ticket);
        assertEquals("TICKET-001", ticket.getTicketNumber());
        assertEquals(99.99, ticket.getPrice());
        assertEquals(Ticket.TicketStatus.AVAILABLE, ticket.getStatus());
        assertEquals(event, ticket.getEvent());
    }

    @Test
    @DisplayName("Should create an empty ticket")
    void testEmptyTicketConstruction() {
        Ticket emptyTicket = new Ticket();
        assertNotNull(emptyTicket);
        assertNull(emptyTicket.getId());
        assertNull(emptyTicket.getTicketNumber());
    }

    @Test
    @DisplayName("Should set and get ticket ID")
    void testSetAndGetId() {
        ticket.setId(1L);
        assertEquals(1L, ticket.getId());
    }

    @Test
    @DisplayName("Should set and get event")
    void testSetAndGetEvent() {
        LocalDateTime eventDate2 = LocalDateTime.of(2026, 7, 20, 20, 0);
        Event newEvent = new Event("Rock Concert", "A rock concert", eventDate2,
                "MSG", 5000, 150.0);
        ticket.setEvent(newEvent);
        assertEquals(newEvent, ticket.getEvent());
    }

    @Test
    @DisplayName("Should set and get ticket number")
    void testSetAndGetTicketNumber() {
        ticket.setTicketNumber("TICKET-999");
        assertEquals("TICKET-999", ticket.getTicketNumber());
    }

    @Test
    @DisplayName("Should set and get ticket status")
    void testSetAndGetStatus() {
        ticket.setStatus(Ticket.TicketStatus.BOOKED);
        assertEquals(Ticket.TicketStatus.BOOKED, ticket.getStatus());
    }

    @Test
    @DisplayName("Should transition from AVAILABLE to BOOKED")
    void testAvailableToBookedTransition() {
        assertEquals(Ticket.TicketStatus.AVAILABLE, ticket.getStatus());
        ticket.setStatus(Ticket.TicketStatus.BOOKED);
        assertEquals(Ticket.TicketStatus.BOOKED, ticket.getStatus());
    }

    @Test
    @DisplayName("Should transition from BOOKED to AVAILABLE")
    void testBookedToAvailableTransition() {
        ticket.setStatus(Ticket.TicketStatus.BOOKED);
        ticket.setStatus(Ticket.TicketStatus.AVAILABLE);
        assertEquals(Ticket.TicketStatus.AVAILABLE, ticket.getStatus());
    }

    @Test
    @DisplayName("Should set and get price")
    void testSetAndGetPrice() {
        ticket.setPrice(149.99);
        assertEquals(149.99, ticket.getPrice());
    }

    @Test
    @DisplayName("Should handle zero price")
    void testZeroPrice() {
        ticket.setPrice(0.0);
        assertEquals(0.0, ticket.getPrice());
    }

    @Test
    @DisplayName("Should handle high price")
    void testHighPrice() {
        ticket.setPrice(999.99);
        assertEquals(999.99, ticket.getPrice());
    }
}

