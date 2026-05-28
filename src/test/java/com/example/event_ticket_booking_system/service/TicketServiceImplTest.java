package com.example.event_ticket_booking_system.service;

import com.example.event_ticket_booking_system.dto.TicketDTO;
import com.example.event_ticket_booking_system.entity.Booking;
import com.example.event_ticket_booking_system.entity.Event;
import com.example.event_ticket_booking_system.entity.Ticket;
import com.example.event_ticket_booking_system.repository.BookingRepository;
import com.example.event_ticket_booking_system.repository.EventRepository;
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
@DisplayName("TicketServiceImpl Tests")
class TicketServiceImplTest {

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private TicketServiceImpl ticketService;

    private Event event;
    private Ticket ticket;
    private TicketDTO createRequest;

    @BeforeEach
    void setUp() {
        event = new Event("Java Conference", "Annual Java event",
                LocalDateTime.of(2026, 8, 20, 18, 0), "San Francisco", 500, 99.99);
        event.setId(1L);

        ticket = new Ticket(event, "CONF-0001", 99.99);
        ticket.setId(10L);

        createRequest = new TicketDTO();
        createRequest.setEventId(1L);
        createRequest.setTicketNumber("CONF-0001");
        createRequest.setPrice(99.99);
    }

    // ── createTicket ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("Should create a ticket with default AVAILABLE status")
    void testCreateTicketDefaultStatus() {
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(ticketRepository.existsByTicketNumberIgnoreCase("CONF-0001")).thenReturn(false);
        when(ticketRepository.save(any(Ticket.class))).thenAnswer(inv -> {
            Ticket t = inv.getArgument(0);
            t.setId(10L);
            return t;
        });

        TicketDTO result = ticketService.createTicket(createRequest);

        assertNotNull(result);
        assertEquals("CONF-0001", result.getTicketNumber());
        assertEquals(Ticket.TicketStatus.AVAILABLE, result.getStatus());
        assertTrue(result.getAvailable());
        verify(ticketRepository).save(any(Ticket.class));
    }

    @Test
    @DisplayName("Should create a ticket with explicit status")
    void testCreateTicketWithExplicitStatus() {
        createRequest.setStatus(Ticket.TicketStatus.RESERVED);
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(ticketRepository.existsByTicketNumberIgnoreCase("CONF-0001")).thenReturn(false);
        when(ticketRepository.save(any(Ticket.class))).thenAnswer(inv -> inv.getArgument(0));

        TicketDTO result = ticketService.createTicket(createRequest);

        assertEquals(Ticket.TicketStatus.RESERVED, result.getStatus());
    }

    @Test
    @DisplayName("Should reject creation when event not found")
    void testCreateTicketEventNotFound() {
        when(eventRepository.findById(1L)).thenReturn(Optional.empty());
        when(ticketRepository.existsByTicketNumberIgnoreCase("CONF-0001")).thenReturn(false);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> ticketService.createTicket(createRequest));
        assertTrue(ex.getMessage().startsWith("Event not found"));
    }

    @Test
    @DisplayName("Should reject creation when ticket number already exists")
    void testCreateTicketDuplicateNumber() {
        when(ticketRepository.existsByTicketNumberIgnoreCase("CONF-0001")).thenReturn(true);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> ticketService.createTicket(createRequest));
        assertEquals("Ticket number already exists.", ex.getMessage());
    }

    @Test
    @DisplayName("Should reject creation when event id is null")
    void testCreateTicketNullEventId() {
        createRequest.setEventId(null);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> ticketService.createTicket(createRequest));
        assertEquals("Event id is required.", ex.getMessage());
    }

    @Test
    @DisplayName("Should reject creation when ticket number is blank")
    void testCreateTicketBlankNumber() {
        createRequest.setTicketNumber("  ");

        assertThrows(IllegalArgumentException.class, () -> ticketService.createTicket(createRequest));
    }

    @Test
    @DisplayName("Should reject creation when price is null")
    void testCreateTicketNullPrice() {
        createRequest.setPrice(null);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> ticketService.createTicket(createRequest));
        assertEquals("Ticket price is required.", ex.getMessage());
    }

    @Test
    @DisplayName("Should reject creation when price is negative")
    void testCreateTicketNegativePrice() {
        createRequest.setPrice(-5.0);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> ticketService.createTicket(createRequest));
        assertEquals("Ticket price cannot be negative.", ex.getMessage());
    }

    // ── getTicketById ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("Should get ticket by id")
    void testGetTicketByIdFound() {
        when(ticketRepository.findById(10L)).thenReturn(Optional.of(ticket));

        TicketDTO result = ticketService.getTicketById(10L);

        assertEquals("CONF-0001", result.getTicketNumber());
        assertEquals(99.99, result.getPrice());
    }

    @Test
    @DisplayName("Should throw when ticket not found by id")
    void testGetTicketByIdNotFound() {
        when(ticketRepository.findById(99L)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> ticketService.getTicketById(99L));
        assertTrue(ex.getMessage().startsWith("Ticket not found"));
    }

    // ── getAllTickets ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("Should return all tickets as DTOs")
    void testGetAllTickets() {
        when(ticketRepository.findAll()).thenReturn(List.of(ticket));

        List<TicketDTO> result = ticketService.getAllTickets();

        assertEquals(1, result.size());
        assertEquals("CONF-0001", result.get(0).getTicketNumber());
    }

    // ── getTicketsByEvent ─────────────────────────────────────────────────────

    @Test
    @DisplayName("Should return tickets for a specific event")
    void testGetTicketsByEvent() {
        when(ticketRepository.findByEventId(1L)).thenReturn(List.of(ticket));

        List<TicketDTO> result = ticketService.getTicketsByEvent(1L);

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getEventId());
    }

    // ── getTicketsByStatus ────────────────────────────────────────────────────

    @Test
    @DisplayName("Should return tickets by status")
    void testGetTicketsByStatus() {
        when(ticketRepository.findByStatus(Ticket.TicketStatus.AVAILABLE))
                .thenReturn(List.of(ticket));

        List<TicketDTO> result = ticketService.getTicketsByStatus(Ticket.TicketStatus.AVAILABLE);

        assertEquals(1, result.size());
        assertEquals(Ticket.TicketStatus.AVAILABLE, result.get(0).getStatus());
    }

    // ── getAvailableTickets ───────────────────────────────────────────────────

    @Test
    @DisplayName("Should return all available tickets")
    void testGetAvailableTickets() {
        when(ticketRepository.findByStatus(Ticket.TicketStatus.AVAILABLE))
                .thenReturn(List.of(ticket));

        List<TicketDTO> result = ticketService.getAvailableTickets();

        assertEquals(1, result.size());
        assertTrue(result.get(0).getAvailable());
    }

    // ── getAvailableTicketsByEvent ────────────────────────────────────────────

    @Test
    @DisplayName("Should return available tickets for a specific event")
    void testGetAvailableTicketsByEvent() {
        when(ticketRepository.findByEventIdAndStatus(1L, Ticket.TicketStatus.AVAILABLE))
                .thenReturn(List.of(ticket));

        List<TicketDTO> result = ticketService.getAvailableTicketsByEvent(1L);

        assertEquals(1, result.size());
    }

    // ── getTicketsForCurrentUser ──────────────────────────────────────────────

    @Test
    @DisplayName("Should return tickets for a user via bookings")
    void testGetTicketsForCurrentUser() {
        Booking booking = new Booking(ticket, event, "John Doe", "john@example.com",
                LocalDateTime.now());
        when(bookingRepository.findByCustomerEmailIgnoreCase("john@example.com"))
                .thenReturn(List.of(booking));

        List<TicketDTO> result = ticketService.getTicketsForCurrentUser("john@example.com");

        assertEquals(1, result.size());
        assertEquals("CONF-0001", result.get(0).getTicketNumber());
    }

    @Test
    @DisplayName("Should throw when username is blank")
    void testGetTicketsForCurrentUserBlank() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> ticketService.getTicketsForCurrentUser("  "));
        assertEquals("Username is required.", ex.getMessage());
    }

    @Test
    @DisplayName("Should throw when username is null")
    void testGetTicketsForCurrentUserNull() {
        assertThrows(IllegalArgumentException.class,
                () -> ticketService.getTicketsForCurrentUser(null));
    }

    // ── updateTicket ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("Should update ticket number, price and status")
    void testUpdateTicketSuccess() {
        when(ticketRepository.findById(10L)).thenReturn(Optional.of(ticket));
        when(ticketRepository.findByTicketNumberIgnoreCase("CONF-9999")).thenReturn(Optional.empty());
        when(ticketRepository.save(any(Ticket.class))).thenAnswer(inv -> inv.getArgument(0));

        TicketDTO update = new TicketDTO();
        update.setTicketNumber("CONF-9999");
        update.setPrice(149.99);
        update.setStatus(Ticket.TicketStatus.BOOKED);

        TicketDTO result = ticketService.updateTicket(10L, update);

        assertEquals("CONF-9999", result.getTicketNumber());
        assertEquals(149.99, result.getPrice());
    }

    @Test
    @DisplayName("Should throw when updating ticket that does not exist")
    void testUpdateTicketNotFound() {
        when(ticketRepository.findById(99L)).thenReturn(Optional.empty());

        TicketDTO update = new TicketDTO();
        update.setTicketNumber("X");
        update.setPrice(10.0);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> ticketService.updateTicket(99L, update));
        assertTrue(ex.getMessage().startsWith("Ticket not found"));
    }

    @Test
    @DisplayName("Should reject update when ticket number belongs to a different ticket")
    void testUpdateTicketDuplicateNumber() {
        Ticket other = new Ticket(event, "CONF-EXISTING", 50.0);
        other.setId(99L);
        when(ticketRepository.findById(10L)).thenReturn(Optional.of(ticket));
        when(ticketRepository.findByTicketNumberIgnoreCase("CONF-EXISTING"))
                .thenReturn(Optional.of(other));

        TicketDTO update = new TicketDTO();
        update.setTicketNumber("CONF-EXISTING");
        update.setPrice(50.0);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> ticketService.updateTicket(10L, update));
        assertEquals("Ticket number already exists.", ex.getMessage());
    }

    @Test
    @DisplayName("Should allow update when same ticket keeps its own number")
    void testUpdateTicketSameNumber() {
        when(ticketRepository.findById(10L)).thenReturn(Optional.of(ticket));
        when(ticketRepository.findByTicketNumberIgnoreCase("CONF-0001"))
                .thenReturn(Optional.of(ticket));
        when(ticketRepository.save(any(Ticket.class))).thenAnswer(inv -> inv.getArgument(0));

        TicketDTO update = new TicketDTO();
        update.setTicketNumber("CONF-0001");
        update.setPrice(89.99);

        TicketDTO result = ticketService.updateTicket(10L, update);

        assertEquals("CONF-0001", result.getTicketNumber());
    }

    // ── updateTicketStatus ────────────────────────────────────────────────────

    @Test
    @DisplayName("Should update ticket status to RESERVED")
    void testUpdateTicketStatusToReserved() {
        when(ticketRepository.findById(10L)).thenReturn(Optional.of(ticket));
        when(ticketRepository.save(any(Ticket.class))).thenAnswer(inv -> inv.getArgument(0));

        TicketDTO result = ticketService.updateTicketStatus(10L, Ticket.TicketStatus.RESERVED);

        assertEquals(Ticket.TicketStatus.RESERVED, result.getStatus());
    }

    @Test
    @DisplayName("Should update ticket status to BOOKED")
    void testUpdateTicketStatusToBooked() {
        when(ticketRepository.findById(10L)).thenReturn(Optional.of(ticket));
        when(ticketRepository.save(any(Ticket.class))).thenAnswer(inv -> inv.getArgument(0));

        TicketDTO result = ticketService.updateTicketStatus(10L, Ticket.TicketStatus.BOOKED);

        assertEquals(Ticket.TicketStatus.BOOKED, result.getStatus());
    }

    @Test
    @DisplayName("Should update ticket status to CANCELLED")
    void testUpdateTicketStatusToCancelled() {
        when(ticketRepository.findById(10L)).thenReturn(Optional.of(ticket));
        when(ticketRepository.save(any(Ticket.class))).thenAnswer(inv -> inv.getArgument(0));

        TicketDTO result = ticketService.updateTicketStatus(10L, Ticket.TicketStatus.CANCELLED);

        assertEquals(Ticket.TicketStatus.CANCELLED, result.getStatus());
    }

    @Test
    @DisplayName("Should update ticket status back to AVAILABLE")
    void testUpdateTicketStatusToAvailable() {
        ticket.setStatus(Ticket.TicketStatus.BOOKED);
        when(ticketRepository.findById(10L)).thenReturn(Optional.of(ticket));
        when(ticketRepository.save(any(Ticket.class))).thenAnswer(inv -> inv.getArgument(0));

        TicketDTO result = ticketService.updateTicketStatus(10L, Ticket.TicketStatus.AVAILABLE);

        assertEquals(Ticket.TicketStatus.AVAILABLE, result.getStatus());
    }

    @Test
    @DisplayName("Should throw when trying to reserve a non-available ticket")
    void testUpdateTicketStatusReserveNotAvailable() {
        ticket.setStatus(Ticket.TicketStatus.BOOKED);
        when(ticketRepository.findById(10L)).thenReturn(Optional.of(ticket));

        assertThrows(IllegalStateException.class,
                () -> ticketService.updateTicketStatus(10L, Ticket.TicketStatus.RESERVED));
    }

    @Test
    @DisplayName("Should throw when status is null")
    void testUpdateTicketStatusNull() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> ticketService.updateTicketStatus(10L, null));
        assertEquals("Ticket status must not be null.", ex.getMessage());
    }

    // ── isTicketAvailable ─────────────────────────────────────────────────────

    @Test
    @DisplayName("Should return true when ticket is available")
    void testIsTicketAvailableTrue() {
        when(ticketRepository.findById(10L)).thenReturn(Optional.of(ticket));

        assertTrue(ticketService.isTicketAvailable(10L));
    }

    @Test
    @DisplayName("Should return false when ticket is booked")
    void testIsTicketAvailableFalse() {
        ticket.setStatus(Ticket.TicketStatus.BOOKED);
        when(ticketRepository.findById(10L)).thenReturn(Optional.of(ticket));

        assertFalse(ticketService.isTicketAvailable(10L));
    }

    @Test
    @DisplayName("Should throw when checking availability of missing ticket")
    void testIsTicketAvailableNotFound() {
        when(ticketRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> ticketService.isTicketAvailable(99L));
    }

    // ── deleteTicket ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("Should delete an existing ticket")
    void testDeleteTicketSuccess() {
        when(ticketRepository.findById(10L)).thenReturn(Optional.of(ticket));

        ticketService.deleteTicket(10L);

        verify(ticketRepository).delete(ticket);
    }

    @Test
    @DisplayName("Should throw when deleting a non-existent ticket")
    void testDeleteTicketNotFound() {
        when(ticketRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> ticketService.deleteTicket(99L));
        verify(ticketRepository, never()).delete(any());
    }
}
