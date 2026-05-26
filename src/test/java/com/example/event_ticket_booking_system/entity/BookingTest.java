package com.example.event_ticket_booking_system.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Booking Entity Tests")
class BookingTest {

    private Booking booking;
    private Event event;
    private Ticket ticket;
    private LocalDateTime bookingDate;

    @BeforeEach
    void setUp() {
        LocalDateTime eventDate = LocalDateTime.of(2026, 6, 15, 19, 0);
        event = new Event("Summer Music Festival", "An amazing music festival", eventDate,
                "Central Park", 1000, 99.99);
        ticket = new Ticket(event, "TICKET-001", 99.99);
        bookingDate = LocalDateTime.of(2026, 5, 26, 10, 30);
        booking = new Booking(ticket, event, "John Doe", "john@example.com", bookingDate);
    }

    @Test
    @DisplayName("Should create a booking with all fields")
    void testBookingConstruction() {
        assertNotNull(booking);
        assertEquals(ticket, booking.getTicket());
        assertEquals(event, booking.getEvent());
        assertEquals("John Doe", booking.getCustomerName());
        assertEquals("john@example.com", booking.getCustomerEmail());
        assertEquals(bookingDate, booking.getBookingDate());
        assertEquals(Booking.BookingStatus.CONFIRMED, booking.getStatus());
    }

    @Test
    @DisplayName("Should create an empty booking")
    void testEmptyBookingConstruction() {
        Booking emptyBooking = new Booking();
        assertNotNull(emptyBooking);
        assertNull(emptyBooking.getId());
        assertNull(emptyBooking.getCustomerName());
    }

    @Test
    @DisplayName("Should set and get booking ID")
    void testSetAndGetId() {
        booking.setId(1L);
        assertEquals(1L, booking.getId());
    }

    @Test
    @DisplayName("Should set and get ticket")
    void testSetAndGetTicket() {
        LocalDateTime eventDate2 = LocalDateTime.of(2026, 7, 20, 20, 0);
        Event newEvent = new Event("Rock Concert", "A rock concert", eventDate2, "MSG", 5000, 150.0);
        Ticket newTicket = new Ticket(newEvent, "TICKET-999", 150.0);
        booking.setTicket(newTicket);
        assertEquals(newTicket, booking.getTicket());
    }

    @Test
    @DisplayName("Should set and get event")
    void testSetAndGetEvent() {
        LocalDateTime eventDate2 = LocalDateTime.of(2026, 7, 20, 20, 0);
        Event newEvent = new Event("Rock Concert", "A rock concert", eventDate2, "MSG", 5000, 150.0);
        booking.setEvent(newEvent);
        assertEquals(newEvent, booking.getEvent());
    }

    @Test
    @DisplayName("Should set and get customer name")
    void testSetAndGetCustomerName() {
        booking.setCustomerName("Jane Smith");
        assertEquals("Jane Smith", booking.getCustomerName());
    }

    @Test
    @DisplayName("Should set and get customer email")
    void testSetAndGetCustomerEmail() {
        booking.setCustomerEmail("jane@example.com");
        assertEquals("jane@example.com", booking.getCustomerEmail());
    }

    @Test
    @DisplayName("Should set and get booking date")
    void testSetAndGetBookingDate() {
        LocalDateTime newBookingDate = LocalDateTime.of(2026, 5, 27, 14, 15);
        booking.setBookingDate(newBookingDate);
        assertEquals(newBookingDate, booking.getBookingDate());
    }

    @Test
    @DisplayName("Should set and get booking status")
    void testSetAndGetStatus() {
        booking.setStatus(Booking.BookingStatus.CANCELLED);
        assertEquals(Booking.BookingStatus.CANCELLED, booking.getStatus());
    }

    @Test
    @DisplayName("Should default to CONFIRMED status")
    void testDefaultStatus() {
        assertEquals(Booking.BookingStatus.CONFIRMED, booking.getStatus());
    }

    @Test
    @DisplayName("Should transition from CONFIRMED to CANCELLED")
    void testConfirmedToCancelledTransition() {
        assertEquals(Booking.BookingStatus.CONFIRMED, booking.getStatus());
        booking.setStatus(Booking.BookingStatus.CANCELLED);
        assertEquals(Booking.BookingStatus.CANCELLED, booking.getStatus());
    }

    @Test
    @DisplayName("Should transition from CANCELLED to CONFIRMED")
    void testCancelledToConfirmedTransition() {
        booking.setStatus(Booking.BookingStatus.CANCELLED);
        booking.setStatus(Booking.BookingStatus.CONFIRMED);
        assertEquals(Booking.BookingStatus.CONFIRMED, booking.getStatus());
    }

    @Test
    @DisplayName("Should handle different customer names")
    void testVariousCustomerNames() {
        booking.setCustomerName("Alice");
        assertEquals("Alice", booking.getCustomerName());
        booking.setCustomerName("Bob Johnson");
        assertEquals("Bob Johnson", booking.getCustomerName());
    }

    @Test
    @DisplayName("Should handle different email formats")
    void testVariousEmailFormats() {
        booking.setCustomerEmail("user.name@example.com");
        assertEquals("user.name@example.com", booking.getCustomerEmail());
        booking.setCustomerEmail("another+email@domain.co.uk");
        assertEquals("another+email@domain.co.uk", booking.getCustomerEmail());
    }
}


