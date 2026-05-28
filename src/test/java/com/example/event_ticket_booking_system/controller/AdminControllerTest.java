package com.example.event_ticket_booking_system.controller;

import com.example.event_ticket_booking_system.dto.AdminStatsDTO;
import com.example.event_ticket_booking_system.repository.BookingRepository;
import com.example.event_ticket_booking_system.repository.EventRepository;
import com.example.event_ticket_booking_system.repository.TicketRepository;
import com.example.event_ticket_booking_system.service.AdminService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminController.class)
@DisplayName("AdminController Tests")
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AdminService adminService;

    @MockBean
    private EventRepository eventRepository;

    @MockBean
    private TicketRepository ticketRepository;

    @MockBean
    private BookingRepository bookingRepository;

    private AdminStatsDTO sampleStats;

    @BeforeEach
    void setUp() {
        sampleStats = new AdminStatsDTO(3L, 33L, 31L, 2L, 2L);
    }

    // ── GET /api/admin/stats ──────────────────────────────────────────────────

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("GET /api/admin/stats - should return dashboard stats for admin")
    void testGetDashboardStatsAsAdmin() throws Exception {
        when(adminService.getDashboardStats()).thenReturn(sampleStats);

        mockMvc.perform(get("/api/admin/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalEvents").value(3))
                .andExpect(jsonPath("$.totalTickets").value(33))
                .andExpect(jsonPath("$.availableTickets").value(31))
                .andExpect(jsonPath("$.bookedTickets").value(2))
                .andExpect(jsonPath("$.totalBookings").value(2));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("GET /api/admin/stats - should return 403 for non-admin user")
    void testGetDashboardStatsAsUser() throws Exception {
        mockMvc.perform(get("/api/admin/stats"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("GET /api/admin/stats - should return 401 when not authenticated")
    void testGetDashboardStatsUnauthenticated() throws Exception {
        mockMvc.perform(get("/api/admin/stats"))
                .andExpect(status().isUnauthorized());
    }

    // ── DELETE /api/admin/events/{id} ─────────────────────────────────────────

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("DELETE /api/admin/events/1 - should delete event and return 204")
    void testDeleteEventSuccess() throws Exception {
        when(eventRepository.existsById(1L)).thenReturn(true);
        doNothing().when(eventRepository).deleteById(1L);

        mockMvc.perform(delete("/api/admin/events/1").with(csrf()))
                .andExpect(status().isNoContent());

        verify(eventRepository).deleteById(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("DELETE /api/admin/events/99 - should return 404 when event not found")
    void testDeleteEventNotFound() throws Exception {
        when(eventRepository.existsById(99L)).thenReturn(false);

        mockMvc.perform(delete("/api/admin/events/99").with(csrf()))
                .andExpect(status().isNotFound());

        verify(eventRepository, never()).deleteById(any());
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("DELETE /api/admin/events/1 - should return 403 for non-admin user")
    void testDeleteEventAsUser() throws Exception {
        mockMvc.perform(delete("/api/admin/events/1").with(csrf()))
                .andExpect(status().isForbidden());
    }

    // ── DELETE /api/admin/tickets/{id} ────────────────────────────────────────

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("DELETE /api/admin/tickets/1 - should delete ticket and return 204")
    void testDeleteTicketSuccess() throws Exception {
        when(ticketRepository.existsById(1L)).thenReturn(true);
        doNothing().when(ticketRepository).deleteById(1L);

        mockMvc.perform(delete("/api/admin/tickets/1").with(csrf()))
                .andExpect(status().isNoContent());

        verify(ticketRepository).deleteById(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("DELETE /api/admin/tickets/99 - should return 404 when ticket not found")
    void testDeleteTicketNotFound() throws Exception {
        when(ticketRepository.existsById(99L)).thenReturn(false);

        mockMvc.perform(delete("/api/admin/tickets/99").with(csrf()))
                .andExpect(status().isNotFound());

        verify(ticketRepository, never()).deleteById(any());
    }

    // ── DELETE /api/admin/bookings/{id} ───────────────────────────────────────

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("DELETE /api/admin/bookings/1 - should delete booking and return 204")
    void testDeleteBookingSuccess() throws Exception {
        when(bookingRepository.existsById(1L)).thenReturn(true);
        doNothing().when(bookingRepository).deleteById(1L);

        mockMvc.perform(delete("/api/admin/bookings/1").with(csrf()))
                .andExpect(status().isNoContent());

        verify(bookingRepository).deleteById(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("DELETE /api/admin/bookings/99 - should return 404 when booking not found")
    void testDeleteBookingNotFound() throws Exception {
        when(bookingRepository.existsById(99L)).thenReturn(false);

        mockMvc.perform(delete("/api/admin/bookings/99").with(csrf()))
                .andExpect(status().isNotFound());

        verify(bookingRepository, never()).deleteById(any());
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("DELETE /api/admin/bookings/1 - should return 403 for non-admin user")
    void testDeleteBookingAsUser() throws Exception {
        mockMvc.perform(delete("/api/admin/bookings/1").with(csrf()))
                .andExpect(status().isForbidden());
    }
}