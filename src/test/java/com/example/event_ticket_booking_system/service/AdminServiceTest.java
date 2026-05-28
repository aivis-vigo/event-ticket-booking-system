package com.example.event_ticket_booking_system.service;

import com.example.event_ticket_booking_system.dto.AdminStatsDTO;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AdminService Tests")
class AdminServiceTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private AdminService adminService;

    @BeforeEach
    void setUp() {
        when(eventRepository.count()).thenReturn(3L);
        when(ticketRepository.count()).thenReturn(33L);
        when(ticketRepository.countByStatus(Ticket.TicketStatus.AVAILABLE)).thenReturn(31L);
        when(ticketRepository.countByStatus(Ticket.TicketStatus.BOOKED)).thenReturn(2L);
        when(bookingRepository.count()).thenReturn(2L);
    }

    @Test
    @DisplayName("Should return dashboard stats with correct values")
    void testGetDashboardStats() {
        AdminStatsDTO stats = adminService.getDashboardStats();

        assertNotNull(stats);
        assertEquals(3L, stats.getTotalEvents());
        assertEquals(33L, stats.getTotalTickets());
        assertEquals(31L, stats.getAvailableTickets());
        assertEquals(2L, stats.getBookedTickets());
        assertEquals(2L, stats.getTotalBookings());
    }

    @Test
    @DisplayName("Should call all repositories exactly once")
    void testGetDashboardStatsCallsRepositories() {
        adminService.getDashboardStats();

        verify(eventRepository).count();
        verify(ticketRepository).count();
        verify(ticketRepository).countByStatus(Ticket.TicketStatus.AVAILABLE);
        verify(ticketRepository).countByStatus(Ticket.TicketStatus.BOOKED);
        verify(bookingRepository).count();
    }

    @Test
    @DisplayName("Should return zeros when all counts are zero")
    void testGetDashboardStatsAllZero() {
        when(eventRepository.count()).thenReturn(0L);
        when(ticketRepository.count()).thenReturn(0L);
        when(ticketRepository.countByStatus(Ticket.TicketStatus.AVAILABLE)).thenReturn(0L);
        when(ticketRepository.countByStatus(Ticket.TicketStatus.BOOKED)).thenReturn(0L);
        when(bookingRepository.count()).thenReturn(0L);

        AdminStatsDTO stats = adminService.getDashboardStats();

        assertEquals(0L, stats.getTotalEvents());
        assertEquals(0L, stats.getTotalTickets());
        assertEquals(0L, stats.getAvailableTickets());
        assertEquals(0L, stats.getBookedTickets());
        assertEquals(0L, stats.getTotalBookings());
    }

    @Test
    @DisplayName("Should correctly reflect that booked + available equals total tickets")
    void testGetDashboardStatsTicketCounts() {
        AdminStatsDTO stats = adminService.getDashboardStats();

        // available (31) + booked (2) = 33 (totalTickets)
        assertEquals(stats.getTotalTickets(),
                stats.getAvailableTickets() + stats.getBookedTickets());
    }

    @Test
    @DisplayName("Should handle large counts without overflow")
    void testGetDashboardStatsLargeCounts() {
        long large = 1_000_000L;
        when(eventRepository.count()).thenReturn(large);
        when(ticketRepository.count()).thenReturn(large * 100);
        when(ticketRepository.countByStatus(Ticket.TicketStatus.AVAILABLE)).thenReturn(large * 80);
        when(ticketRepository.countByStatus(Ticket.TicketStatus.BOOKED)).thenReturn(large * 20);
        when(bookingRepository.count()).thenReturn(large * 20);

        AdminStatsDTO stats = adminService.getDashboardStats();

        assertEquals(large, stats.getTotalEvents());
        assertEquals(large * 100, stats.getTotalTickets());
    }
}