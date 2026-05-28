package com.example.event_ticket_booking_system.controller;

import com.example.event_ticket_booking_system.dto.TicketDTO;
import com.example.event_ticket_booking_system.entity.Ticket;
import com.example.event_ticket_booking_system.service.TicketService;
import org.springframework.security.core.Authentication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/*
 * This controller is responsible for all ticket API endpoints.
 *
 * Instead of working directly with TicketRepository,
 * it uses TicketService.
 *
 * This is better because:
 * - Controller handles HTTP requests and responses.
 * - Service contains business logic.
 * - Repository only works with the database.
 */
@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    private final TicketService ticketService;

    /*
     * Spring automatically gives us TicketService here.
     * This is constructor dependency injection.
     *
     * It is better than @Autowired on a field because:
     * - the dependency is required;
     * - the field can be final;
     * - the class is easier to test.
     */
    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    /*
     * Gets tickets from the system.
     *
     * Possible requests:
     * GET /api/tickets
     * -> returns all tickets
     *
     * GET /api/tickets?eventId=1
     * -> returns tickets for event with id 1
     *
     * GET /api/tickets?status=AVAILABLE
     * -> returns only tickets with AVAILABLE status
     *
     * GET /api/tickets?eventId=1&status=AVAILABLE
     * -> returns available tickets for event with id 1
     */
    @GetMapping
    public ResponseEntity<List<TicketDTO>> getTickets(
            @RequestParam(required = false) Long eventId,
            @RequestParam(required = false) Ticket.TicketStatus status
    ) {
        if (eventId != null && status != null) {
            if (status == Ticket.TicketStatus.AVAILABLE) {
                return ResponseEntity.ok(ticketService.getAvailableTicketsByEvent(eventId));
            }

            List<TicketDTO> filteredTickets = ticketService.getTicketsByEvent(eventId)
                    .stream()
                    .filter(ticket -> ticket.getStatus() == status)
                    .toList();

            return ResponseEntity.ok(filteredTickets);
        }

        if (eventId != null) {
            return ResponseEntity.ok(ticketService.getTicketsByEvent(eventId));
        }

        if (status != null) {
            return ResponseEntity.ok(ticketService.getTicketsByStatus(status));
        }

        return ResponseEntity.ok(ticketService.getAllTickets());
    }
    /*
     * Gets tickets for the currently logged-in user.
     *
     * Example:
     * GET /api/tickets/my
     *
     * The current user is taken from Spring Security Authentication.
     * If user is logged in, returns only tickets connected to this user.
     * If user is not logged in, returns 401 Unauthorized.
     */
    @GetMapping("/my")
    public ResponseEntity<?> getMyTickets(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User is not authenticated.");
        }

        return ResponseEntity.ok(ticketService.getTicketsForCurrentUser(authentication.getName()));
    }
    /*
     * Gets one ticket by its id.
     *
     * Example:
     * GET /api/tickets/5
     *
     * If ticket exists, returns 200 OK.
     * If ticket does not exist, returns 404 Not Found.
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getTicketById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(ticketService.getTicketById(id));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    /*
     * Gets all tickets that are currently available.
     *
     * Example:
     * GET /api/tickets/available
     */
    @GetMapping("/available")
    public ResponseEntity<List<TicketDTO>> getAvailableTickets() {
        return ResponseEntity.ok(ticketService.getAvailableTickets());
    }

    /*
     * Gets available tickets for one specific event.
     *
     * Example:
     * GET /api/tickets/event/1/available
     */
    @GetMapping("/event/{eventId}/available")
    public ResponseEntity<List<TicketDTO>> getAvailableTicketsByEvent(@PathVariable Long eventId) {
        return ResponseEntity.ok(ticketService.getAvailableTicketsByEvent(eventId));
    }

    /*
     * Checks if one specific ticket is available.
     *
     * Example:
     * GET /api/tickets/5/available
     *
     * Returns:
     * true  - ticket is available
     * false - ticket is not available
     */
    @GetMapping("/{id}/available")
    public ResponseEntity<?> isTicketAvailable(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(ticketService.isTicketAvailable(id));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    /*
     * Creates a new ticket.
     *
     * Example:
     * POST /api/tickets
     *
     * Request body:
     * {
     *   "eventId": 1,
     *   "ticketNumber": "VIP-001",
     *   "price": 99.99,
     *   "status": "AVAILABLE"
     * }
     *
     * If ticket is created, returns 201 Created.
     * If data is invalid, returns 400 Bad Request.
     */
    @PostMapping
    public ResponseEntity<?> createTicket(@RequestBody TicketDTO ticketDTO) {
        try {
            TicketDTO createdTicket = ticketService.createTicket(ticketDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdTicket);
        } catch (IllegalArgumentException | IllegalStateException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    /*
     * Updates ticket information.
     *
     * Example:
     * PUT /api/tickets/5
     *
     * This can update:
     * - eventId
     * - ticketNumber
     * - price
     * - status
     *
     * If ticket exists, returns updated ticket.
     * If ticket does not exist, returns 404 Not Found.
     * If data is invalid, returns 400 Bad Request.
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateTicket(
            @PathVariable Long id,
            @RequestBody TicketDTO ticketDTO
    ) {
        try {
            return ResponseEntity.ok(ticketService.updateTicket(id, ticketDTO));
        } catch (IllegalArgumentException ex) {
            if (ex.getMessage().startsWith("Ticket not found")) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    /*
     * Updates only ticket status.
     *
     * Example:
     * PATCH /api/tickets/5/status?status=RESERVED
     *
     * Possible statuses:
     * AVAILABLE
     * RESERVED
     * BOOKED
     * CANCELLED
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateTicketStatus(
            @PathVariable Long id,
            @RequestParam Ticket.TicketStatus status
    ) {
        try {
            return ResponseEntity.ok(ticketService.updateTicketStatus(id, status));
        } catch (IllegalArgumentException ex) {
            if (ex.getMessage().startsWith("Ticket not found")) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.badRequest().body(ex.getMessage());
        } catch (IllegalStateException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    /*
     * Deletes ticket by id.
     *
     * Example:
     * DELETE /api/tickets/5
     *
     * If ticket is deleted, returns 204 No Content.
     * If ticket does not exist, returns 404 Not Found.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTicket(@PathVariable Long id) {
        try {
            ticketService.deleteTicket(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.notFound().build();
        }
    }
}