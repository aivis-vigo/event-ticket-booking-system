package com.example.event_ticket_booking_system.service;

import com.example.event_ticket_booking_system.dto.BookingDTO;
import com.example.event_ticket_booking_system.entity.Booking;
import com.example.event_ticket_booking_system.entity.Event;
import com.example.event_ticket_booking_system.entity.Ticket;
import com.example.event_ticket_booking_system.repository.BookingRepository;
import com.example.event_ticket_booking_system.repository.TicketRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("BookingServiceImpl Tests")
class BookingServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private TicketRepository ticketRepository;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private Event event;
    private Ticket ticket;
    private BookingDTO createRequest;

    @BeforeEach
    void setUp() {
        event = new Event("Summer Music Festival", "An amazing music festival",
                LocalDateTime.of(2026, 6, 15, 19, 0), "Central Park", 1000, 99.99);
        event.setId(3L);

        ticket = new Ticket(event, "TICKET-001", 99.99);
        ticket.setId(2L);

        createRequest = new BookingDTO();
        createRequest.setTicketId(2L);
        createRequest.setCustomerName("John Doe");
        createRequest.setCustomerEmail("john@example.com");
    }

    @Test
    @DisplayName("Should create a booking and book the ticket")
    void testCreateBookingSuccess() {
        when(ticketRepository.findById(2L)).thenReturn(Optional.of(ticket));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(inv -> inv.getArgument(0));

        BookingDTO result = bookingService.createBooking(createRequest);

        assertNotNull(result);
        assertEquals(2L, result.getTicketId());
        assertEquals("TICKET-001", result.getTicketNumber());
        assertEquals(3L, result.getEventId());
        assertEquals("John Doe", result.getCustomerName());
        assertEquals(Booking.BookingStatus.CONFIRMED, result.getStatus());
        assertEquals(Ticket.TicketStatus.BOOKED, ticket.getStatus());

        verify(ticketRepository).save(ticket);
        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    @DisplayName("Should reject booking when ticket is not available")
    void testCreateBookingTicketNotAvailable() {
        ticket.setStatus(Ticket.TicketStatus.BOOKED);
        when(ticketRepository.findById(2L)).thenReturn(Optional.of(ticket));

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> bookingService.createBooking(createRequest));

        assertEquals("Ticket is not available for booking.", ex.getMessage());
        verify(bookingRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should reject booking when ticket does not exist")
    void testCreateBookingTicketNotFound() {
        when(ticketRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> bookingService.createBooking(createRequest));
        verify(bookingRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should reject booking when ticket id is missing")
    void testCreateBookingMissingTicketId() {
        createRequest.setTicketId(null);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> bookingService.createBooking(createRequest));
        assertEquals("Ticket id is required.", ex.getMessage());
    }

    @Test
    @DisplayName("Should reject booking when customer name is blank")
    void testCreateBookingBlankName() {
        createRequest.setCustomerName("  ");

        assertThrows(IllegalArgumentException.class,
                () -> bookingService.createBooking(createRequest));
    }

    @Test
    @DisplayName("Should reject booking when customer email is blank")
    void testCreateBookingBlankEmail() {
        createRequest.setCustomerEmail("");

        assertThrows(IllegalArgumentException.class,
                () -> bookingService.createBooking(createRequest));
    }

    @Test
    @DisplayName("Should get a booking by id")
    void testGetBookingById() {
        Booking booking = sampleBooking();
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        BookingDTO result = bookingService.getBookingById(1L);

        assertEquals("John Doe", result.getCustomerName());
        assertEquals("TICKET-001", result.getTicketNumber());
    }

    @Test
    @DisplayName("Should throw when booking id is not found")
    void testGetBookingByIdNotFound() {
        when(bookingRepository.findById(99L)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> bookingService.getBookingById(99L));
        assertTrue(ex.getMessage().startsWith("Booking not found"));
    }

    @Test
    @DisplayName("Should return all bookings as DTOs")
    void testGetAllBookings() {
        when(bookingRepository.findAll()).thenReturn(List.of(sampleBooking()));

        List<BookingDTO> result = bookingService.getAllBookings();

        assertEquals(1, result.size());
        assertEquals("john@example.com", result.get(0).getCustomerEmail());
    }

    @Test
    @DisplayName("Should return bookings for an event")
    void testGetBookingsByEvent() {
        when(bookingRepository.findByEventId(3L)).thenReturn(List.of(sampleBooking()));

        List<BookingDTO> result = bookingService.getBookingsByEvent(3L);

        assertEquals(1, result.size());
        assertEquals(3L, result.get(0).getEventId());
    }

    @Test
    @DisplayName("Should return bookings for a customer")
    void testGetBookingsForCustomer() {
        when(bookingRepository.findByCustomerEmailIgnoreCase("john@example.com"))
                .thenReturn(List.of(sampleBooking()));

        List<BookingDTO> result = bookingService.getBookingsForCustomer("john@example.com");

        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("Should reject customer lookup with blank email")
    void testGetBookingsForCustomerBlank() {
        assertThrows(IllegalArgumentException.class,
                () -> bookingService.getBookingsForCustomer("  "));
    }

    @Test
    @DisplayName("Should update customer information on a booking")
    void testUpdateBooking() {
        Booking booking = sampleBooking();
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(inv -> inv.getArgument(0));

        BookingDTO update = new BookingDTO();
        update.setCustomerName("Jane Smith");
        update.setCustomerEmail("jane@example.com");

        BookingDTO result = bookingService.updateBooking(1L, update);

        assertEquals("Jane Smith", result.getCustomerName());
        assertEquals("jane@example.com", result.getCustomerEmail());
    }

    @Test
    @DisplayName("Should cancel a booking and free its ticket")
    void testCancelBooking() {
        Booking booking = sampleBooking();
        ticket.setStatus(Ticket.TicketStatus.BOOKED);
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(inv -> inv.getArgument(0));

        BookingDTO result = bookingService.cancelBooking(1L);

        assertEquals(Booking.BookingStatus.CANCELLED, result.getStatus());
        assertEquals(Ticket.TicketStatus.AVAILABLE, ticket.getStatus());
        verify(ticketRepository).save(ticket);
    }

    @Test
    @DisplayName("Should reject cancelling an already cancelled booking")
    void testCancelAlreadyCancelled() {
        Booking booking = sampleBooking();
        booking.setStatus(Booking.BookingStatus.CANCELLED);
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> bookingService.cancelBooking(1L));
        assertEquals("Booking is already cancelled.", ex.getMessage());
        verify(ticketRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should delete an existing booking")
    void testDeleteBooking() {
        Booking booking = sampleBooking();
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        bookingService.deleteBooking(1L);

        verify(bookingRepository).delete(booking);
    }

    @Test
    @DisplayName("Should throw when deleting a missing booking")
    void testDeleteBookingNotFound() {
        when(bookingRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> bookingService.deleteBooking(99L));
        verify(bookingRepository, never()).delete(any());
    }

    private Booking sampleBooking() {
        Booking booking = new Booking(ticket, event, "John Doe", "john@example.com",
                LocalDateTime.of(2026, 5, 26, 10, 30));
        booking.setId(1L);
        return booking;
    }
}
