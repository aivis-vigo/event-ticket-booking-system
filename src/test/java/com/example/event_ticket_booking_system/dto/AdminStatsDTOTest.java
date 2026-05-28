package com.example.event_ticket_booking_system.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("AdminStatsDTO Tests")
class AdminStatsDTOTest {

    private AdminStatsDTO adminStatsDTO;

    @BeforeEach
    void setUp() {
        adminStatsDTO = new AdminStatsDTO(10, 100, 75, 25, 50);
    }

    @Test
    @DisplayName("Constructor should initialize all fields correctly")
    void testConstructor() {
        AdminStatsDTO stats = new AdminStatsDTO(5, 50, 40, 10, 25);

        assertEquals(5, stats.getTotalEvents());
        assertEquals(50, stats.getTotalTickets());
        assertEquals(40, stats.getAvailableTickets());
        assertEquals(10, stats.getBookedTickets());
        assertEquals(25, stats.getTotalBookings());
    }

    @Test
    @DisplayName("Constructor with zero values should work correctly")
    void testConstructorWithZeroes() {
        AdminStatsDTO stats = new AdminStatsDTO(0, 0, 0, 0, 0);

        assertEquals(0, stats.getTotalEvents());
        assertEquals(0, stats.getTotalTickets());
        assertEquals(0, stats.getAvailableTickets());
        assertEquals(0, stats.getBookedTickets());
        assertEquals(0, stats.getTotalBookings());
    }

    @Test
    @DisplayName("Constructor with large numbers should work correctly")
    void testConstructorWithLargeNumbers() {
        long largeNumber = 1_000_000;
        AdminStatsDTO stats = new AdminStatsDTO(largeNumber, largeNumber * 10, largeNumber * 8,
                                               largeNumber * 2, largeNumber * 5);

        assertEquals(largeNumber, stats.getTotalEvents());
        assertEquals(largeNumber * 10, stats.getTotalTickets());
        assertEquals(largeNumber * 8, stats.getAvailableTickets());
        assertEquals(largeNumber * 2, stats.getBookedTickets());
        assertEquals(largeNumber * 5, stats.getTotalBookings());
    }

    @Test
    @DisplayName("getTotalEvents should return correct value")
    void testGetTotalEvents() {
        assertEquals(10, adminStatsDTO.getTotalEvents());
    }

    @Test
    @DisplayName("getTotalTickets should return correct value")
    void testGetTotalTickets() {
        assertEquals(100, adminStatsDTO.getTotalTickets());
    }

    @Test
    @DisplayName("getAvailableTickets should return correct value")
    void testGetAvailableTickets() {
        assertEquals(75, adminStatsDTO.getAvailableTickets());
    }

    @Test
    @DisplayName("getBookedTickets should return correct value")
    void testGetBookedTickets() {
        assertEquals(25, adminStatsDTO.getBookedTickets());
    }

    @Test
    @DisplayName("getTotalBookings should return correct value")
    void testGetTotalBookings() {
        assertEquals(50, adminStatsDTO.getTotalBookings());
    }

    @Test
    @DisplayName("All getters should return consistent values")
    void testAllGettersConsistent() {
        long totalEvents = 7;
        long totalTickets = 280;
        long availableTickets = 210;
        long bookedTickets = 70;
        long totalBookings = 35;

        AdminStatsDTO stats = new AdminStatsDTO(totalEvents, totalTickets, availableTickets,
                                               bookedTickets, totalBookings);

        assertEquals(totalEvents, stats.getTotalEvents());
        assertEquals(totalTickets, stats.getTotalTickets());
        assertEquals(availableTickets, stats.getAvailableTickets());
        assertEquals(bookedTickets, stats.getBookedTickets());
        assertEquals(totalBookings, stats.getTotalBookings());
    }

    @Test
    @DisplayName("Available and booked tickets should sum to total tickets")
    void testTicketsSumCorrectly() {
        AdminStatsDTO stats = new AdminStatsDTO(5, 100, 60, 40, 20);

        assertEquals(100, stats.getAvailableTickets() + stats.getBookedTickets());
    }
}

