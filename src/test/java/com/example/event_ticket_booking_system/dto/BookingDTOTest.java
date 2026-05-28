package com.example.event_ticket_booking_system.dto;
import com.example.event_ticket_booking_system.entity.Booking;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("BookingDTO Tests")
class BookingDTOTest {

    private BookingDTO bookingDTO;
    private LocalDateTime bookingDate;

    @BeforeEach
    void setUp() {
        bookingDate = LocalDateTime.of(2026, 5, 26, 10, 30);
        bookingDTO = new BookingDTO(1L, 2L, "TICKET-001", 3L, "Summer Music Festival",
                "John Doe", "john@example.com", bookingDate, Booking.BookingStatus.CONFIRMED);
    }

    @Test
    @DisplayName("Should create a booking DTO with all fields")
    void testFullConstructor() {
        assertEquals(1L, bookingDTO.getId());
        assertEquals(2L, bookingDTO.getTicketId());
        assertEquals("TICKET-001", bookingDTO.getTicketNumber());
        assertEquals(3L, bookingDTO.getEventId());
        assertEquals("Summer Music Festival", bookingDTO.getEventName());
        assertEquals("John Doe", bookingDTO.getCustomerName());
        assertEquals("john@example.com", bookingDTO.getCustomerEmail());
        assertEquals(bookingDate, bookingDTO.getBookingDate());
        assertEquals(Booking.BookingStatus.CONFIRMED, bookingDTO.getStatus());
    }

    @Test
    @DisplayName("Should create an empty booking DTO")
    void testEmptyConstructor() {
        BookingDTO empty = new BookingDTO();
        assertNotNull(empty);
        assertNull(empty.getId());
        assertNull(empty.getTicketId());
        assertNull(empty.getCustomerName());
        assertNull(empty.getStatus());
    }

    @Test
    @DisplayName("Should set and get id")
    void testSetAndGetId() {
        bookingDTO.setId(99L);
        assertEquals(99L, bookingDTO.getId());
    }

    @Test
    @DisplayName("Should set and get ticket id")
    void testSetAndGetTicketId() {
        bookingDTO.setTicketId(42L);
        assertEquals(42L, bookingDTO.getTicketId());
    }

    @Test
    @DisplayName("Should set and get ticket number")
    void testSetAndGetTicketNumber() {
        bookingDTO.setTicketNumber("TICKET-999");
        assertEquals("TICKET-999", bookingDTO.getTicketNumber());
    }

    @Test
    @DisplayName("Should set and get event id")
    void testSetAndGetEventId() {
        bookingDTO.setEventId(7L);
        assertEquals(7L, bookingDTO.getEventId());
    }

    @Test
    @DisplayName("Should set and get event name")
    void testSetAndGetEventName() {
        bookingDTO.setEventName("Rock Concert");
        assertEquals("Rock Concert", bookingDTO.getEventName());
    }

    @Test
    @DisplayName("Should set and get customer name")
    void testSetAndGetCustomerName() {
        bookingDTO.setCustomerName("Jane Smith");
        assertEquals("Jane Smith", bookingDTO.getCustomerName());
    }

    @Test
    @DisplayName("Should set and get customer email")
    void testSetAndGetCustomerEmail() {
        bookingDTO.setCustomerEmail("jane@example.com");
        assertEquals("jane@example.com", bookingDTO.getCustomerEmail());
    }

    @Test
    @DisplayName("Should set and get booking date")
    void testSetAndGetBookingDate() {
        LocalDateTime newDate = LocalDateTime.of(2026, 6, 1, 12, 0);
        bookingDTO.setBookingDate(newDate);
        assertEquals(newDate, bookingDTO.getBookingDate());
    }

    @Test
    @DisplayName("Should set and get status")
    void testSetAndGetStatus() {
        bookingDTO.setStatus(Booking.BookingStatus.CANCELLED);
        assertEquals(Booking.BookingStatus.CANCELLED, bookingDTO.getStatus());
    }
}
