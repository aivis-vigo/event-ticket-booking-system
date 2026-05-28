package com.example.event_ticket_booking_system.service;

import com.example.event_ticket_booking_system.dto.TicketDTO;
import com.example.event_ticket_booking_system.entity.Ticket;

import java.util.List;

/*
 * POLYMORPHISM / ABSTRACTION:
 * Controller will work with this interface, not directly with implementation.
 */
public interface TicketService {

    TicketDTO createTicket(TicketDTO ticketDTO);

    TicketDTO getTicketById(Long id);

    List<TicketDTO> getAllTickets();

    List<TicketDTO> getTicketsByEvent(Long eventId);

    List<TicketDTO> getTicketsByStatus(Ticket.TicketStatus status);

    List<TicketDTO> getAvailableTickets();

    List<TicketDTO> getAvailableTicketsByEvent(Long eventId);

    List<TicketDTO> getTicketsForCurrentUser(String username);

    TicketDTO updateTicket(Long id, TicketDTO ticketDTO);

    TicketDTO updateTicketStatus(Long id, Ticket.TicketStatus status);

    Boolean isTicketAvailable(Long id);

    void deleteTicket(Long id);
}