package com.example.event_ticket_booking_system.service;

import com.example.event_ticket_booking_system.dto.TicketDTO;
import com.example.event_ticket_booking_system.entity.Event;
import com.example.event_ticket_booking_system.entity.Ticket;
import com.example.event_ticket_booking_system.repository.EventRepository;
import com.example.event_ticket_booking_system.repository.TicketRepository;
import com.example.event_ticket_booking_system.entity.Booking;
import com.example.event_ticket_booking_system.repository.BookingRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/*
 * POLYMORPHISM:
 * TicketServiceImpl implements TicketService.
 * The controller will use the TicketService interface,
 * but Spring will inject this TicketServiceImpl class at runtime.
 */
@Service
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;
    private final EventRepository eventRepository;
    private final BookingRepository bookingRepository;
    /*
     * DEPENDENCY INJECTION:
     * Constructor injection is used here.
     * We do not create repository objects manually with "new".
     * Spring provides them automatically.
     */
    public TicketServiceImpl(
            TicketRepository ticketRepository,
            EventRepository eventRepository,
            BookingRepository bookingRepository
    ) {
        this.ticketRepository = ticketRepository;
        this.eventRepository = eventRepository;
        this.bookingRepository = bookingRepository;
    }

    /*
     * TICKET CREATION:
     * Creates a new ticket for an existing event.
     */
    @Override
    public TicketDTO createTicket(TicketDTO ticketDTO) {
        validateTicketForCreate(ticketDTO);

        Event event = eventRepository.findById(ticketDTO.getEventId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Event not found with id: " + ticketDTO.getEventId()
                ));

        Ticket ticket = new Ticket();
        ticket.setEvent(event);
        ticket.setTicketNumber(ticketDTO.getTicketNumber().trim());
        ticket.setPrice(ticketDTO.getPrice());

        if (ticketDTO.getStatus() == null) {
            ticket.setStatus(Ticket.TicketStatus.AVAILABLE);
        } else {
            ticket.setStatus(ticketDTO.getStatus());
        }

        Ticket savedTicket = ticketRepository.save(ticket);
        return toDTO(savedTicket);
    }

    /*
     * GET ONE TICKET:
     * Finds ticket by id or throws exception.
     */
    @Override
    public TicketDTO getTicketById(Long id) {
        Ticket ticket = findTicketOrThrow(id);
        return toDTO(ticket);
    }

    /*
     * GET ALL TICKETS:
     * Converts entity list to DTO list.
     */
    @Override
    public List<TicketDTO> getAllTickets() {
        return ticketRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /*
     * EVENT FILTERING:
     * Returns all tickets for one event.
     */
    @Override
    public List<TicketDTO> getTicketsByEvent(Long eventId) {
        return ticketRepository.findByEventId(eventId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /*
     * STATUS FILTERING:
     * Returns tickets by status: AVAILABLE, RESERVED, BOOKED, CANCELLED.
     */
    @Override
    public List<TicketDTO> getTicketsByStatus(Ticket.TicketStatus status) {
        return ticketRepository.findByStatus(status)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /*
     * AVAILABILITY LOGIC:
     * Returns all available tickets.
     */
    @Override
    public List<TicketDTO> getAvailableTickets() {
        return ticketRepository.findByStatus(Ticket.TicketStatus.AVAILABLE)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /*
     * AVAILABILITY LOGIC:
     * Returns available tickets for a specific event.
     */
    @Override
    public List<TicketDTO> getAvailableTicketsByEvent(Long eventId) {
        return ticketRepository.findByEventIdAndStatus(eventId, Ticket.TicketStatus.AVAILABLE)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
    /*
     * USER TICKETS:
     * Returns tickets for a specific user.
     */
    @Override
    public List<TicketDTO> getTicketsForCurrentUser(String username) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username is required.");
        }

        return bookingRepository.findByCustomerEmailIgnoreCase(username)
                .stream()
                .map(Booking::getTicket)
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
    /*
     * UPDATE TICKET:
     * Updates ticket number, price, event and status.
     */
    @Override
    public TicketDTO updateTicket(Long id, TicketDTO ticketDTO) {
        Ticket existingTicket = findTicketOrThrow(id);

        validateTicketForUpdate(ticketDTO, existingTicket);

        if (ticketDTO.getEventId() != null) {
            Event event = eventRepository.findById(ticketDTO.getEventId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Event not found with id: " + ticketDTO.getEventId()
                    ));

            existingTicket.setEvent(event);
        }

        existingTicket.setTicketNumber(ticketDTO.getTicketNumber().trim());
        existingTicket.setPrice(ticketDTO.getPrice());

        if (ticketDTO.getStatus() != null) {
            existingTicket.setStatus(ticketDTO.getStatus());
        }

        Ticket updatedTicket = ticketRepository.save(existingTicket);
        return toDTO(updatedTicket);
    }

    /*
     * STATUS MANAGEMENT:
     * Changes only the ticket status.
     * Where possible, it uses methods from Ticket entity.
     */
    @Override
    public TicketDTO updateTicketStatus(Long id, Ticket.TicketStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("Ticket status must not be null.");
        }

        Ticket ticket = findTicketOrThrow(id);

        /*
         * BUSINESS LOGIC:
         * We use entity methods instead of directly changing status everywhere.
         * This keeps status rules inside Ticket entity.
         */
        switch (status) {
            case RESERVED -> ticket.reserve();
            case BOOKED -> ticket.book();
            case CANCELLED -> ticket.cancel();
            case AVAILABLE -> ticket.setStatus(Ticket.TicketStatus.AVAILABLE);
        }

        Ticket updatedTicket = ticketRepository.save(ticket);
        return toDTO(updatedTicket);
    }

    /*
     * AVAILABILITY CHECK:
     * Returns true if ticket status is AVAILABLE.
     */
    @Override
    public Boolean isTicketAvailable(Long id) {
        Ticket ticket = findTicketOrThrow(id);
        return ticket.isAvailable();
    }

    /*
     * DELETE TICKET:
     * Deletes ticket by id.
     */
    @Override
    public void deleteTicket(Long id) {
        Ticket ticket = findTicketOrThrow(id);
        ticketRepository.delete(ticket);
    }

    /*
     * HELPER METHOD:
     * Avoids repeating findById logic in every method.
     */
    private Ticket findTicketOrThrow(Long id) {
        return ticketRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Ticket not found with id: " + id
                ));
    }

    /*
     * VALIDATION:
     * Validates ticket before creation.
     */
    private void validateTicketForCreate(TicketDTO dto) {
        if (dto.getEventId() == null) {
            throw new IllegalArgumentException("Event id is required.");
        }

        validateCommonTicketFields(dto);

        if (ticketRepository.existsByTicketNumberIgnoreCase(dto.getTicketNumber().trim())) {
            throw new IllegalArgumentException("Ticket number already exists.");
        }
    }

    /*
     * VALIDATION:
     * Validates ticket before update.
     */
    private void validateTicketForUpdate(TicketDTO dto, Ticket existingTicket) {
        validateCommonTicketFields(dto);

        ticketRepository.findByTicketNumberIgnoreCase(dto.getTicketNumber().trim())
                .ifPresent(ticketWithSameNumber -> {
                    if (!ticketWithSameNumber.getId().equals(existingTicket.getId())) {
                        throw new IllegalArgumentException("Ticket number already exists.");
                    }
                });
    }

    /*
     * VALIDATION:
     * Common validation rules for create and update.
     */
    private void validateCommonTicketFields(TicketDTO dto) {
        if (dto.getTicketNumber() == null || dto.getTicketNumber().isBlank()) {
            throw new IllegalArgumentException("Ticket number must not be blank.");
        }

        if (dto.getPrice() == null) {
            throw new IllegalArgumentException("Ticket price is required.");
        }

        if (dto.getPrice() < 0) {
            throw new IllegalArgumentException("Ticket price cannot be negative.");
        }
    }

    /*
     * MAPPING:
     * Converts Ticket entity to TicketDTO.
     * This prevents exposing the full entity through API.
     */
    private TicketDTO toDTO(Ticket ticket) {
        TicketDTO dto = new TicketDTO();

        dto.setId(ticket.getId());

        if (ticket.getEvent() != null) {
            dto.setEventId(ticket.getEvent().getId());
            dto.setEventName(ticket.getEvent().getName());
        }

        dto.setTicketNumber(ticket.getTicketNumber());
        dto.setStatus(ticket.getStatus());
        dto.setPrice(ticket.getPrice());
        dto.setAvailable(ticket.isAvailable());

        return dto;
    }
}