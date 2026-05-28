package com.example.event_ticket_booking_system.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("EventDTO Tests")
class EventDTOTest {

    private EventDTO eventDTO;
    private LocalDateTime eventDate;

    @BeforeEach
    void setUp() {
        eventDate = LocalDateTime.of(2026, 8, 20, 18, 0);
        eventDTO = new EventDTO(1L, "Java Conference", "Annual Java event",
                eventDate, "San Francisco", 500, 99.99);
    }

    @Test
    @DisplayName("Should create an EventDTO with all fields via full constructor")
    void testFullConstructor() {
        assertEquals(1L, eventDTO.getId());
        assertEquals("Java Conference", eventDTO.getName());
        assertEquals("Annual Java event", eventDTO.getDescription());
        assertEquals(eventDate, eventDTO.getEventDate());
        assertEquals("San Francisco", eventDTO.getLocation());
        assertEquals(500, eventDTO.getTotalTickets());
        assertEquals(99.99, eventDTO.getTicketPrice());
    }

    @Test
    @DisplayName("Should create an empty EventDTO with no-args constructor")
    void testEmptyConstructor() {
        EventDTO empty = new EventDTO();
        assertNotNull(empty);
        assertNull(empty.getId());
        assertNull(empty.getName());
        assertNull(empty.getDescription());
        assertNull(empty.getEventDate());
        assertNull(empty.getLocation());
        assertNull(empty.getTotalTickets());
        assertNull(empty.getTicketPrice());
    }

    @Test
    @DisplayName("Should set and get id")
    void testSetAndGetId() {
        eventDTO.setId(42L);
        assertEquals(42L, eventDTO.getId());
    }

    @Test
    @DisplayName("Should set and get name")
    void testSetAndGetName() {
        eventDTO.setName("Spring Boot Workshop");
        assertEquals("Spring Boot Workshop", eventDTO.getName());
    }

    @Test
    @DisplayName("Should set and get description")
    void testSetAndGetDescription() {
        eventDTO.setDescription("Updated description");
        assertEquals("Updated description", eventDTO.getDescription());
    }

    @Test
    @DisplayName("Should set and get event date")
    void testSetAndGetEventDate() {
        LocalDateTime newDate = LocalDateTime.of(2026, 12, 31, 23, 59);
        eventDTO.setEventDate(newDate);
        assertEquals(newDate, eventDTO.getEventDate());
    }

    @Test
    @DisplayName("Should set and get location")
    void testSetAndGetLocation() {
        eventDTO.setLocation("New York Convention Center");
        assertEquals("New York Convention Center", eventDTO.getLocation());
    }

    @Test
    @DisplayName("Should set and get total tickets")
    void testSetAndGetTotalTickets() {
        eventDTO.setTotalTickets(1000);
        assertEquals(1000, eventDTO.getTotalTickets());
    }

    @Test
    @DisplayName("Should set and get ticket price")
    void testSetAndGetTicketPrice() {
        eventDTO.setTicketPrice(149.50);
        assertEquals(149.50, eventDTO.getTicketPrice());
    }

    @Test
    @DisplayName("Should handle zero ticket price")
    void testZeroTicketPrice() {
        eventDTO.setTicketPrice(0.0);
        assertEquals(0.0, eventDTO.getTicketPrice());
    }

    @Test
    @DisplayName("Should handle large total tickets value")
    void testLargeTotalTickets() {
        eventDTO.setTotalTickets(100_000);
        assertEquals(100_000, eventDTO.getTotalTickets());
    }

    @Test
    @DisplayName("Should allow overwriting name with new value")
    void testOverwriteName() {
        eventDTO.setName("Renamed Event");
        eventDTO.setName("Final Name");
        assertEquals("Final Name", eventDTO.getName());
    }
}