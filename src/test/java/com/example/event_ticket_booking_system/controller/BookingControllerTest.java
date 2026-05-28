package com.example.event_ticket_booking_system.controller;

import com.example.event_ticket_booking_system.dto.BookingDTO;
import com.example.event_ticket_booking_system.entity.Booking;
import com.example.event_ticket_booking_system.service.BookingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookingController.class)
@DisplayName("BookingController Tests")
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingService bookingService;

    private ObjectMapper objectMapper;
    private BookingDTO confirmedBooking;
    private BookingDTO cancelledBooking;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        LocalDateTime bookingDate = LocalDateTime.of(2026, 5, 26, 10, 30);

        confirmedBooking = new BookingDTO(1L, 2L, "CONF-0001", 3L, "Java Conference",
                "John Doe", "john@example.com", bookingDate, Booking.BookingStatus.CONFIRMED);

        cancelledBooking = new BookingDTO(1L, 2L, "CONF-0001", 3L, "Java Conference",
                "John Doe", "john@example.com", bookingDate, Booking.BookingStatus.CANCELLED);
    }

    // ── GET /api/bookings ─────────────────────────────────────────────────────

    @Test
    @WithMockUser
    @DisplayName("GET /api/bookings - should return all bookings")
    void testGetAllBookings() throws Exception {
        when(bookingService.getAllBookings()).thenReturn(List.of(confirmedBooking));

        mockMvc.perform(get("/api/bookings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].customerName").value("John Doe"))
                .andExpect(jsonPath("$[0].status").value("CONFIRMED"));
    }

    @Test
    @WithMockUser
    @DisplayName("GET /api/bookings?eventId=3 - should return bookings for event")
    void testGetBookingsByEvent() throws Exception {
        when(bookingService.getBookingsByEvent(3L)).thenReturn(List.of(confirmedBooking));

        mockMvc.perform(get("/api/bookings").param("eventId", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].eventId").value(3));

        verify(bookingService).getBookingsByEvent(3L);
        verify(bookingService, never()).getAllBookings();
    }

    @Test
    @DisplayName("GET /api/bookings - should return 401 when not authenticated")
    void testGetBookingsUnauthenticated() throws Exception {
        mockMvc.perform(get("/api/bookings"))
                .andExpect(status().isUnauthorized());
    }

    // ── GET /api/bookings/my ──────────────────────────────────────────────────

    @Test
    @WithMockUser(username = "john@example.com")
    @DisplayName("GET /api/bookings/my - should return bookings for current user")
    void testGetMyBookingsAuthenticated() throws Exception {
        when(bookingService.getBookingsForCustomer("john@example.com"))
                .thenReturn(List.of(confirmedBooking));

        mockMvc.perform(get("/api/bookings/my"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].customerEmail").value("john@example.com"));
    }

    // ── GET /api/bookings/{id} ────────────────────────────────────────────────

    @Test
    @WithMockUser
    @DisplayName("GET /api/bookings/1 - should return booking by id")
    void testGetBookingByIdFound() throws Exception {
        when(bookingService.getBookingById(1L)).thenReturn(confirmedBooking);

        mockMvc.perform(get("/api/bookings/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.ticketNumber").value("CONF-0001"));
    }

    @Test
    @WithMockUser
    @DisplayName("GET /api/bookings/99 - should return 404 when not found")
    void testGetBookingByIdNotFound() throws Exception {
        when(bookingService.getBookingById(99L))
                .thenThrow(new IllegalArgumentException("Booking not found with id: 99"));

        mockMvc.perform(get("/api/bookings/99"))
                .andExpect(status().isNotFound());
    }

    // ── POST /api/bookings ────────────────────────────────────────────────────

    @Test
    @WithMockUser
    @DisplayName("POST /api/bookings - should create booking and return 201")
    void testCreateBookingSuccess() throws Exception {
        when(bookingService.createBooking(any(BookingDTO.class))).thenReturn(confirmedBooking);

        BookingDTO request = new BookingDTO();
        request.setTicketId(2L);
        request.setCustomerName("John Doe");
        request.setCustomerEmail("john@example.com");

        mockMvc.perform(post("/api/bookings")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("CONFIRMED"));
    }

    @Test
    @WithMockUser
    @DisplayName("POST /api/bookings - should return 400 when ticket is not available")
    void testCreateBookingTicketNotAvailable() throws Exception {
        when(bookingService.createBooking(any(BookingDTO.class)))
                .thenThrow(new IllegalStateException("Ticket is not available for booking."));

        BookingDTO request = new BookingDTO();
        request.setTicketId(2L);
        request.setCustomerName("John Doe");
        request.setCustomerEmail("john@example.com");

        mockMvc.perform(post("/api/bookings")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    @DisplayName("POST /api/bookings - should return 400 when ticket id is missing")
    void testCreateBookingMissingTicketId() throws Exception {
        when(bookingService.createBooking(any(BookingDTO.class)))
                .thenThrow(new IllegalArgumentException("Ticket id is required."));

        BookingDTO request = new BookingDTO();
        request.setCustomerName("John Doe");
        request.setCustomerEmail("john@example.com");

        mockMvc.perform(post("/api/bookings")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    // ── PUT /api/bookings/{id} ────────────────────────────────────────────────

    @Test
    @WithMockUser
    @DisplayName("PUT /api/bookings/1 - should update booking and return 200")
    void testUpdateBookingSuccess() throws Exception {
        BookingDTO updated = new BookingDTO(1L, 2L, "CONF-0001", 3L, "Java Conference",
                "Jane Smith", "jane@example.com", null, Booking.BookingStatus.CONFIRMED);
        when(bookingService.updateBooking(eq(1L), any(BookingDTO.class))).thenReturn(updated);

        mockMvc.perform(put("/api/bookings/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerName").value("Jane Smith"));
    }

    @Test
    @WithMockUser
    @DisplayName("PUT /api/bookings/99 - should return 404 when booking not found")
    void testUpdateBookingNotFound() throws Exception {
        when(bookingService.updateBooking(eq(99L), any(BookingDTO.class)))
                .thenThrow(new IllegalArgumentException("Booking not found with id: 99"));

        mockMvc.perform(put("/api/bookings/99")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(confirmedBooking)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    @DisplayName("PUT /api/bookings/1 - should return 400 for invalid data")
    void testUpdateBookingBadRequest() throws Exception {
        when(bookingService.updateBooking(eq(1L), any(BookingDTO.class)))
                .thenThrow(new IllegalArgumentException("Customer name must not be blank."));

        mockMvc.perform(put("/api/bookings/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(confirmedBooking)))
                .andExpect(status().isBadRequest());
    }

    // ── PATCH /api/bookings/{id}/cancel ───────────────────────────────────────

    @Test
    @WithMockUser
    @DisplayName("PATCH /api/bookings/1/cancel - should cancel booking and return 200")
    void testCancelBookingSuccess() throws Exception {
        when(bookingService.cancelBooking(1L)).thenReturn(cancelledBooking);

        mockMvc.perform(patch("/api/bookings/1/cancel").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"));
    }

    @Test
    @WithMockUser
    @DisplayName("PATCH /api/bookings/99/cancel - should return 404 when booking not found")
    void testCancelBookingNotFound() throws Exception {
        when(bookingService.cancelBooking(99L))
                .thenThrow(new IllegalArgumentException("Booking not found with id: 99"));

        mockMvc.perform(patch("/api/bookings/99/cancel").with(csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    @DisplayName("PATCH /api/bookings/1/cancel - should return 400 when already cancelled")
    void testCancelBookingAlreadyCancelled() throws Exception {
        when(bookingService.cancelBooking(1L))
                .thenThrow(new IllegalStateException("Booking is already cancelled."));

        mockMvc.perform(patch("/api/bookings/1/cancel").with(csrf()))
                .andExpect(status().isBadRequest());
    }

    // ── DELETE /api/bookings/{id} ─────────────────────────────────────────────

    @Test
    @WithMockUser
    @DisplayName("DELETE /api/bookings/1 - should return 204 when deleted")
    void testDeleteBookingSuccess() throws Exception {
        doNothing().when(bookingService).deleteBooking(1L);

        mockMvc.perform(delete("/api/bookings/1").with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser
    @DisplayName("DELETE /api/bookings/99 - should return 404 when not found")
    void testDeleteBookingNotFound() throws Exception {
        doThrow(new IllegalArgumentException("Booking not found with id: 99"))
                .when(bookingService).deleteBooking(99L);

        mockMvc.perform(delete("/api/bookings/99").with(csrf()))
                .andExpect(status().isNotFound());
    }
}