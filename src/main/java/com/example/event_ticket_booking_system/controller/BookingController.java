package com.example.event_ticket_booking_system.controller;
import com.example.event_ticket_booking_system.dto.BookingDTO;
import com.example.event_ticket_booking_system.service.BookingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    /*
     * GET /api/bookings -> all bookings
     * GET /api/bookings?eventId=1 > bookings for one event
     */
    @GetMapping
    public ResponseEntity<List<BookingDTO>> getBookings(@RequestParam(required = false) Long eventId) {
        if (eventId != null) {
            return ResponseEntity.ok(bookingService.getBookingsByEvent(eventId));
        }
        return ResponseEntity.ok(bookingService.getAllBookings());
    }

    /*
     * Bookings for the currently logged-in user.
     * The customer email is taken from Spring Security.
     */
    @GetMapping("/my")
    public ResponseEntity<?> getMyBookings(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User is not authenticated.");
        }
        return ResponseEntity.ok(bookingService.getBookingsForCustomer(authentication.getName()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getBookingById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(bookingService.getBookingById(id));
        } 
        catch (IllegalArgumentException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    /*
     * Creates booking for available ticket
     *
     * Request body:
     * {
     *   "ticketId": 1,
     *   "customerName": "John",
     *   "customerEmail": "john@example.com"
     * }
     */
    @PostMapping
    public ResponseEntity<?> createBooking(@RequestBody BookingDTO bookingDTO) {
        try {
            BookingDTO created = bookingService.createBooking(bookingDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } 
        catch (IllegalArgumentException | IllegalStateException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateBooking(@PathVariable Long id, @RequestBody BookingDTO bookingDTO) {
        try {
            return ResponseEntity.ok(bookingService.updateBooking(id, bookingDTO));
        } 
        catch (IllegalArgumentException ex) {
            if (ex.getMessage().startsWith("Booking not found")) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    /*
     * Cancels booking and frees its ticket.
     * PATCH /api/bookings/5/cancel
     */
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<?> cancelBooking(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(bookingService.cancelBooking(id));
        } 
        catch (IllegalArgumentException ex) {
            return ResponseEntity.notFound().build();
        } 
        catch (IllegalStateException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBooking(@PathVariable Long id) {
        try {
            bookingService.deleteBooking(id);
            return ResponseEntity.noContent().build();
        } 
        catch (IllegalArgumentException ex) {
            return ResponseEntity.notFound().build();
        }
    }
}