package com.example.event_ticket_booking_system.repository;

import com.example.event_ticket_booking_system.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    /*
     * Spring Data JPA automatically creates queries from method names.
     * These methods are needed for ticket search, filtering and validation.
     */

    List<Ticket> findByEventId(Long eventId);

    List<Ticket> findByStatus(Ticket.TicketStatus status);

    List<Ticket> findByEventIdAndStatus(Long eventId, Ticket.TicketStatus status);

    Optional<Ticket> findByTicketNumberIgnoreCase(String ticketNumber);

    boolean existsByTicketNumberIgnoreCase(String ticketNumber);

    long countByStatus(Ticket.TicketStatus status);
}