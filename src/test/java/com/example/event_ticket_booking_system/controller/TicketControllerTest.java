package com.example.event_ticket_booking_system.controller;

import com.example.event_ticket_booking_system.dto.TicketDTO;
import com.example.event_ticket_booking_system.entity.Ticket;
import com.example.event_ticket_booking_system.service.TicketService;
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

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TicketController.class)
@DisplayName("TicketController Tests")
class TicketControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TicketService ticketService;

    private ObjectMapper objectMapper;
    private TicketDTO availableTicket;
    private TicketDTO bookedTicket;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        availableTicket = new TicketDTO(1L, 2L, "Java Conference", "CONF-0001",
                Ticket.TicketStatus.AVAILABLE, 99.99, true);

        bookedTicket = new TicketDTO(2L, 2L, "Java Conference", "CONF-0002",
                Ticket.TicketStatus.BOOKED, 99.99, false);
    }

    // ── GET /api/tickets ──────────────────────────────────────────────────────

    @Test
    @WithMockUser
    @DisplayName("GET /api/tickets - should return all tickets")
    void testGetAllTickets() throws Exception {
        when(ticketService.getAllTickets()).thenReturn(List.of(availableTicket, bookedTicket));

        mockMvc.perform(get("/api/tickets"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @WithMockUser
    @DisplayName("GET /api/tickets?eventId=2 - should return tickets by event")
    void testGetTicketsByEvent() throws Exception {
        when(ticketService.getTicketsByEvent(2L)).thenReturn(List.of(availableTicket));

        mockMvc.perform(get("/api/tickets").param("eventId", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].eventId").value(2));

        verify(ticketService).getTicketsByEvent(2L);
    }

    @Test
    @WithMockUser
    @DisplayName("GET /api/tickets?status=AVAILABLE - should return tickets by status")
    void testGetTicketsByStatus() throws Exception {
        when(ticketService.getTicketsByStatus(Ticket.TicketStatus.AVAILABLE))
                .thenReturn(List.of(availableTicket));

        mockMvc.perform(get("/api/tickets").param("status", "AVAILABLE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("AVAILABLE"));
    }

    @Test
    @WithMockUser
    @DisplayName("GET /api/tickets?eventId=2&status=AVAILABLE - should return available tickets by event")
    void testGetTicketsByEventAndStatusAvailable() throws Exception {
        when(ticketService.getAvailableTicketsByEvent(2L)).thenReturn(List.of(availableTicket));

        mockMvc.perform(get("/api/tickets")
                        .param("eventId", "2")
                        .param("status", "AVAILABLE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].available").value(true));
    }

    @Test
    @WithMockUser
    @DisplayName("GET /api/tickets?eventId=2&status=BOOKED - should filter by event+status in memory")
    void testGetTicketsByEventAndStatusBooked() throws Exception {
        when(ticketService.getTicketsByEvent(2L)).thenReturn(List.of(availableTicket, bookedTicket));

        mockMvc.perform(get("/api/tickets")
                        .param("eventId", "2")
                        .param("status", "BOOKED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].status").value("BOOKED"));
    }

    @Test
    @DisplayName("GET /api/tickets - should return 401 when not authenticated")
    void testGetTicketsUnauthenticated() throws Exception {
        mockMvc.perform(get("/api/tickets"))
                .andExpect(status().isUnauthorized());
    }

    // ── GET /api/tickets/my ───────────────────────────────────────────────────

    @Test
    @WithMockUser(username = "user@example.com")
    @DisplayName("GET /api/tickets/my - should return tickets for current user")
    void testGetMyTicketsAuthenticated() throws Exception {
        when(ticketService.getTicketsForCurrentUser("user@example.com"))
                .thenReturn(List.of(availableTicket));

        mockMvc.perform(get("/api/tickets/my"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].ticketNumber").value("CONF-0001"));
    }

    // ── GET /api/tickets/{id} ─────────────────────────────────────────────────

    @Test
    @WithMockUser
    @DisplayName("GET /api/tickets/1 - should return ticket by id")
    void testGetTicketByIdFound() throws Exception {
        when(ticketService.getTicketById(1L)).thenReturn(availableTicket);

        mockMvc.perform(get("/api/tickets/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ticketNumber").value("CONF-0001"));
    }

    @Test
    @WithMockUser
    @DisplayName("GET /api/tickets/99 - should return 404 when not found")
    void testGetTicketByIdNotFound() throws Exception {
        when(ticketService.getTicketById(99L))
                .thenThrow(new IllegalArgumentException("Ticket not found with id: 99"));

        mockMvc.perform(get("/api/tickets/99"))
                .andExpect(status().isNotFound());
    }

    // ── GET /api/tickets/available ────────────────────────────────────────────

    @Test
    @WithMockUser
    @DisplayName("GET /api/tickets/available - should return available tickets")
    void testGetAvailableTickets() throws Exception {
        when(ticketService.getAvailableTickets()).thenReturn(List.of(availableTicket));

        mockMvc.perform(get("/api/tickets/available"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].available").value(true));
    }

    // ── GET /api/tickets/event/{eventId}/available ────────────────────────────

    @Test
    @WithMockUser
    @DisplayName("GET /api/tickets/event/2/available - should return available tickets for event")
    void testGetAvailableTicketsByEvent() throws Exception {
        when(ticketService.getAvailableTicketsByEvent(2L)).thenReturn(List.of(availableTicket));

        mockMvc.perform(get("/api/tickets/event/2/available"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].eventId").value(2));
    }

    // ── GET /api/tickets/{id}/available ───────────────────────────────────────

    @Test
    @WithMockUser
    @DisplayName("GET /api/tickets/1/available - should return true for available ticket")
    void testIsTicketAvailableTrue() throws Exception {
        when(ticketService.isTicketAvailable(1L)).thenReturn(true);

        mockMvc.perform(get("/api/tickets/1/available"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    @WithMockUser
    @DisplayName("GET /api/tickets/2/available - should return false for booked ticket")
    void testIsTicketAvailableFalse() throws Exception {
        when(ticketService.isTicketAvailable(2L)).thenReturn(false);

        mockMvc.perform(get("/api/tickets/2/available"))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
    }

    @Test
    @WithMockUser
    @DisplayName("GET /api/tickets/99/available - should return 404 when ticket not found")
    void testIsTicketAvailableNotFound() throws Exception {
        when(ticketService.isTicketAvailable(99L))
                .thenThrow(new IllegalArgumentException("Ticket not found with id: 99"));

        mockMvc.perform(get("/api/tickets/99/available"))
                .andExpect(status().isNotFound());
    }

    // ── POST /api/tickets ─────────────────────────────────────────────────────

    @Test
    @WithMockUser
    @DisplayName("POST /api/tickets - should create ticket and return 201")
    void testCreateTicketSuccess() throws Exception {
        when(ticketService.createTicket(any(TicketDTO.class))).thenReturn(availableTicket);

        TicketDTO request = new TicketDTO(null, 2L, null, "CONF-0001", null, 99.99, null);

        mockMvc.perform(post("/api/tickets")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.ticketNumber").value("CONF-0001"));
    }

    @Test
    @WithMockUser
    @DisplayName("POST /api/tickets - should return 400 when data is invalid")
    void testCreateTicketBadRequest() throws Exception {
        when(ticketService.createTicket(any(TicketDTO.class)))
                .thenThrow(new IllegalArgumentException("Event id is required."));

        TicketDTO request = new TicketDTO(null, null, null, "X", null, 10.0, null);

        mockMvc.perform(post("/api/tickets")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    // ── PUT /api/tickets/{id} ─────────────────────────────────────────────────

    @Test
    @WithMockUser
    @DisplayName("PUT /api/tickets/1 - should update ticket and return 200")
    void testUpdateTicketSuccess() throws Exception {
        TicketDTO updated = new TicketDTO(1L, 2L, "Java Conference", "CONF-0001-UPDATED",
                Ticket.TicketStatus.AVAILABLE, 89.99, true);
        when(ticketService.updateTicket(eq(1L), any(TicketDTO.class))).thenReturn(updated);

        mockMvc.perform(put("/api/tickets/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ticketNumber").value("CONF-0001-UPDATED"));
    }

    @Test
    @WithMockUser
    @DisplayName("PUT /api/tickets/99 - should return 404 when ticket not found")
    void testUpdateTicketNotFound() throws Exception {
        when(ticketService.updateTicket(eq(99L), any(TicketDTO.class)))
                .thenThrow(new IllegalArgumentException("Ticket not found with id: 99"));

        mockMvc.perform(put("/api/tickets/99")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(availableTicket)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    @DisplayName("PUT /api/tickets/1 - should return 400 for duplicate ticket number")
    void testUpdateTicketDuplicateNumber() throws Exception {
        when(ticketService.updateTicket(eq(1L), any(TicketDTO.class)))
                .thenThrow(new IllegalArgumentException("Ticket number already exists."));

        mockMvc.perform(put("/api/tickets/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(availableTicket)))
                .andExpect(status().isBadRequest());
    }

    // ── PATCH /api/tickets/{id}/status ────────────────────────────────────────

    @Test
    @WithMockUser
    @DisplayName("PATCH /api/tickets/1/status?status=BOOKED - should update status")
    void testUpdateTicketStatusSuccess() throws Exception {
        TicketDTO updated = new TicketDTO(1L, 2L, "Java Conference", "CONF-0001",
                Ticket.TicketStatus.BOOKED, 99.99, false);
        when(ticketService.updateTicketStatus(1L, Ticket.TicketStatus.BOOKED)).thenReturn(updated);

        mockMvc.perform(patch("/api/tickets/1/status")
                        .with(csrf())
                        .param("status", "BOOKED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("BOOKED"));
    }

    @Test
    @WithMockUser
    @DisplayName("PATCH /api/tickets/99/status - should return 404 when not found")
    void testUpdateTicketStatusNotFound() throws Exception {
        when(ticketService.updateTicketStatus(99L, Ticket.TicketStatus.RESERVED))
                .thenThrow(new IllegalArgumentException("Ticket not found with id: 99"));

        mockMvc.perform(patch("/api/tickets/99/status")
                        .with(csrf())
                        .param("status", "RESERVED"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    @DisplayName("PATCH /api/tickets/1/status - should return 400 for illegal state")
    void testUpdateTicketStatusIllegalState() throws Exception {
        when(ticketService.updateTicketStatus(1L, Ticket.TicketStatus.RESERVED))
                .thenThrow(new IllegalStateException("Only available tickets can be reserved."));

        mockMvc.perform(patch("/api/tickets/1/status")
                        .with(csrf())
                        .param("status", "RESERVED"))
                .andExpect(status().isBadRequest());
    }

    // ── DELETE /api/tickets/{id} ──────────────────────────────────────────────

    @Test
    @WithMockUser
    @DisplayName("DELETE /api/tickets/1 - should return 204 when deleted")
    void testDeleteTicketSuccess() throws Exception {
        doNothing().when(ticketService).deleteTicket(1L);

        mockMvc.perform(delete("/api/tickets/1").with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser
    @DisplayName("DELETE /api/tickets/99 - should return 404 when not found")
    void testDeleteTicketNotFound() throws Exception {
        doThrow(new IllegalArgumentException("Ticket not found with id: 99"))
                .when(ticketService).deleteTicket(99L);

        mockMvc.perform(delete("/api/tickets/99").with(csrf()))
                .andExpect(status().isNotFound());
    }
}