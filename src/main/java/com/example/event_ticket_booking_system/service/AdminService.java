package com.example.event_ticket_booking_system.service;

import com.example.event_ticket_booking_system.dto.AdminStatsDTO;
import com.example.event_ticket_booking_system.entity.Ticket;
import com.example.event_ticket_booking_system.repository.BookingRepository;
import com.example.event_ticket_booking_system.repository.EventRepository;
import com.example.event_ticket_booking_system.repository.TicketRepository;
import org.springframework.stereotype.Service;

@Service
public class AdminService {

    private final EventRepository eventRepository;
    private final TicketRepository ticketRepository;
    private final BookingRepository bookingRepository;

    public AdminService(
            EventRepository eventRepository,
            TicketRepository ticketRepository,
            BookingRepository bookingRepository
    ) {
        this.eventRepository = eventRepository;
        this.ticketRepository = ticketRepository;
        this.bookingRepository = bookingRepository;
    }

    public AdminStatsDTO getDashboardStats() {

        long totalEvents = eventRepository.count();
        long totalTickets = ticketRepository.count();
        long availableTickets = ticketRepository.countByStatus(Ticket.TicketStatus.AVAILABLE);
        long bookedTickets = ticketRepository.countByStatus(Ticket.TicketStatus.BOOKED);
        long totalBookings = bookingRepository.count();

        return new AdminStatsDTO(
                totalEvents,
                totalTickets,
                availableTickets,
                bookedTickets,
                totalBookings
        );
    }
}