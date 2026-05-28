package com.example.event_ticket_booking_system.dto;

import com.example.event_ticket_booking_system.entity.Ticket;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("TicketDTO Tests")
class TicketDTOTest {

    private TicketDTO ticketDTO;

    @BeforeEach
    void setUp() {
        ticketDTO = new TicketDTO(1L, 2L, "Java Conference", "CONF-0001",
                Ticket.TicketStatus.AVAILABLE, 99.99, true);
    }

    @Test
    @DisplayName("Should create a TicketDTO with all fields via full constructor")
    void testFullConstructor() {
        assertEquals(1L, ticketDTO.getId());
        assertEquals(2L, ticketDTO.getEventId());
        assertEquals("Java Conference", ticketDTO.getEventName());
        assertEquals("CONF-0001", ticketDTO.getTicketNumber());
        assertEquals(Ticket.TicketStatus.AVAILABLE, ticketDTO.getStatus());
        assertEquals(99.99, ticketDTO.getPrice());
        assertTrue(ticketDTO.getAvailable());
    }

    @Test
    @DisplayName("Should create an empty TicketDTO with no-args constructor")
    void testEmptyConstructor() {
        TicketDTO empty = new TicketDTO();
        assertNotNull(empty);
        assertNull(empty.getId());
        assertNull(empty.getEventId());
        assertNull(empty.getEventName());
        assertNull(empty.getTicketNumber());
        assertNull(empty.getStatus());
        assertNull(empty.getPrice());
        assertNull(empty.getAvailable());
    }

    @Test
    @DisplayName("Should set and get id")
    void testSetAndGetId() {
        ticketDTO.setId(99L);
        assertEquals(99L, ticketDTO.getId());
    }

    @Test
    @DisplayName("Should set and get event id")
    void testSetAndGetEventId() {
        ticketDTO.setEventId(5L);
        assertEquals(5L, ticketDTO.getEventId());
    }

    @Test
    @DisplayName("Should set and get event name")
    void testSetAndGetEventName() {
        ticketDTO.setEventName("Spring Boot Workshop");
        assertEquals("Spring Boot Workshop", ticketDTO.getEventName());
    }

    @Test
    @DisplayName("Should set and get ticket number")
    void testSetAndGetTicketNumber() {
        ticketDTO.setTicketNumber("VIP-999");
        assertEquals("VIP-999", ticketDTO.getTicketNumber());
    }

    @Test
    @DisplayName("Should set and get status to BOOKED")
    void testSetAndGetStatusBooked() {
        ticketDTO.setStatus(Ticket.TicketStatus.BOOKED);
        assertEquals(Ticket.TicketStatus.BOOKED, ticketDTO.getStatus());
    }

    @Test
    @DisplayName("Should set and get status to RESERVED")
    void testSetAndGetStatusReserved() {
        ticketDTO.setStatus(Ticket.TicketStatus.RESERVED);
        assertEquals(Ticket.TicketStatus.RESERVED, ticketDTO.getStatus());
    }

    @Test
    @DisplayName("Should set and get status to CANCELLED")
    void testSetAndGetStatusCancelled() {
        ticketDTO.setStatus(Ticket.TicketStatus.CANCELLED);
        assertEquals(Ticket.TicketStatus.CANCELLED, ticketDTO.getStatus());
    }

    @Test
    @DisplayName("Should set and get price")
    void testSetAndGetPrice() {
        ticketDTO.setPrice(49.99);
        assertEquals(49.99, ticketDTO.getPrice());
    }

    @Test
    @DisplayName("Should set and get available as false")
    void testSetAndGetAvailableFalse() {
        ticketDTO.setAvailable(false);
        assertFalse(ticketDTO.getAvailable());
    }

    @Test
    @DisplayName("Should set and get available as true")
    void testSetAndGetAvailableTrue() {
        ticketDTO.setAvailable(true);
        assertTrue(ticketDTO.getAvailable());
    }

    @Test
    @DisplayName("Should handle zero price")
    void testZeroPrice() {
        ticketDTO.setPrice(0.0);
        assertEquals(0.0, ticketDTO.getPrice());
    }

    @Test
    @DisplayName("Available flag should reflect BOOKED status when set manually")
    void testAvailableFlagWhenBooked() {
        ticketDTO.setStatus(Ticket.TicketStatus.BOOKED);
        ticketDTO.setAvailable(false);
        assertFalse(ticketDTO.getAvailable());
        assertEquals(Ticket.TicketStatus.BOOKED, ticketDTO.getStatus());
    }
}