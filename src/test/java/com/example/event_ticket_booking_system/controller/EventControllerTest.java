package com.example.event_ticket_booking_system.controller;

import com.example.event_ticket_booking_system.dto.EventDTO;
import com.example.event_ticket_booking_system.service.EventService;
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
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EventController.class)
@DisplayName("EventController Tests")
class EventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EventService eventService;

    private ObjectMapper objectMapper;
    private EventDTO sampleEvent;
    private static final LocalDateTime FUTURE_DATE = LocalDateTime.of(2027, 6, 15, 10, 0);

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        sampleEvent = new EventDTO(1L, "Java Conference", "Annual Java event",
                FUTURE_DATE, "San Francisco", 500, 99.99);
    }

    // ── GET /api/events ───────────────────────────────────────────────────────

    @Test
    @WithMockUser
    @DisplayName("GET /api/events - should return all events")
    void testGetAllEvents() throws Exception {
        when(eventService.getAllEvents()).thenReturn(List.of(sampleEvent));

        mockMvc.perform(get("/api/events"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Java Conference"))
                .andExpect(jsonPath("$[0].location").value("San Francisco"));
    }

    @Test
    @WithMockUser
    @DisplayName("GET /api/events?keyword=java - should search events by keyword")
    void testGetAllEventsWithKeyword() throws Exception {
        when(eventService.searchEvents("java")).thenReturn(List.of(sampleEvent));

        mockMvc.perform(get("/api/events").param("keyword", "java"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Java Conference"));

        verify(eventService).searchEvents("java");
        verify(eventService, never()).getAllEvents();
    }

    @Test
    @WithMockUser
    @DisplayName("GET /api/events?keyword= - blank keyword returns all events")
    void testGetAllEventsWithBlankKeyword() throws Exception {
        when(eventService.getAllEvents()).thenReturn(List.of(sampleEvent));

        mockMvc.perform(get("/api/events").param("keyword", "  "))
                .andExpect(status().isOk());

        verify(eventService).getAllEvents();
    }

    @Test
    @DisplayName("GET /api/events - should return 401 when not authenticated")
    void testGetAllEventsUnauthenticated() throws Exception {
        mockMvc.perform(get("/api/events"))
                .andExpect(status().isUnauthorized());
    }

    // ── GET /api/events/{id} ──────────────────────────────────────────────────

    @Test
    @WithMockUser
    @DisplayName("GET /api/events/1 - should return event by id")
    void testGetEventByIdFound() throws Exception {
        when(eventService.getEventById(1L)).thenReturn(Optional.of(sampleEvent));

        mockMvc.perform(get("/api/events/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Java Conference"));
    }

    @Test
    @WithMockUser
    @DisplayName("GET /api/events/99 - should return 404 when not found")
    void testGetEventByIdNotFound() throws Exception {
        when(eventService.getEventById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/events/99"))
                .andExpect(status().isNotFound());
    }

    // ── GET /api/events/upcoming ──────────────────────────────────────────────

    @Test
    @WithMockUser
    @DisplayName("GET /api/events/upcoming - should return upcoming events")
    void testGetUpcomingEvents() throws Exception {
        when(eventService.getUpcomingEvents()).thenReturn(List.of(sampleEvent));

        mockMvc.perform(get("/api/events/upcoming"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Java Conference"));
    }

    // ── GET /api/events/search ────────────────────────────────────────────────

    @Test
    @WithMockUser
    @DisplayName("GET /api/events/search?location=SF - should filter by location")
    void testSearchEventsByLocation() throws Exception {
        when(eventService.getEventsByLocation("SF")).thenReturn(List.of(sampleEvent));

        mockMvc.perform(get("/api/events/search").param("location", "SF"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].location").value("San Francisco"));
    }

    @Test
    @WithMockUser
    @DisplayName("GET /api/events/search?minPrice=50&maxPrice=150 - should filter by price range")
    void testSearchEventsByPriceRange() throws Exception {
        when(eventService.getEventsByPriceRange(50.0, 150.0)).thenReturn(List.of(sampleEvent));

        mockMvc.perform(get("/api/events/search")
                        .param("minPrice", "50.0")
                        .param("maxPrice", "150.0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].ticketPrice").value(99.99));
    }

    @Test
    @WithMockUser
    @DisplayName("GET /api/events/search with no params - should return all events")
    void testSearchEventsNoParams() throws Exception {
        when(eventService.getAllEvents()).thenReturn(List.of(sampleEvent));

        mockMvc.perform(get("/api/events/search"))
                .andExpect(status().isOk());

        verify(eventService).getAllEvents();
    }

    // ── POST /api/events ──────────────────────────────────────────────────────

    @Test
    @WithMockUser
    @DisplayName("POST /api/events - should create event and return 201")
    void testCreateEventSuccess() throws Exception {
        when(eventService.createEvent(any(EventDTO.class))).thenReturn(sampleEvent);

        EventDTO request = new EventDTO(null, "Java Conference", "Annual Java event",
                FUTURE_DATE, "San Francisco", 500, 99.99);

        mockMvc.perform(post("/api/events")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Java Conference"));
    }

    @Test
    @WithMockUser
    @DisplayName("POST /api/events - should return 400 when service throws IllegalArgumentException")
    void testCreateEventBadRequest() throws Exception {
        when(eventService.createEvent(any(EventDTO.class)))
                .thenThrow(new IllegalArgumentException("Event name must not be blank."));

        EventDTO request = new EventDTO(null, "", "desc", FUTURE_DATE, "SF", 100, 50.0);

        mockMvc.perform(post("/api/events")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    // ── PUT /api/events/{id} ──────────────────────────────────────────────────

    @Test
    @WithMockUser
    @DisplayName("PUT /api/events/1 - should update event and return 200")
    void testUpdateEventSuccess() throws Exception {
        EventDTO updated = new EventDTO(1L, "Updated Name", "Updated desc",
                FUTURE_DATE, "Los Angeles", 300, 79.99);
        when(eventService.updateEvent(eq(1L), any(EventDTO.class))).thenReturn(Optional.of(updated));

        mockMvc.perform(put("/api/events/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Name"));
    }

    @Test
    @WithMockUser
    @DisplayName("PUT /api/events/99 - should return 404 when event not found")
    void testUpdateEventNotFound() throws Exception {
        when(eventService.updateEvent(eq(99L), any(EventDTO.class))).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/events/99")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleEvent)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    @DisplayName("PUT /api/events/1 - should return 400 when data is invalid")
    void testUpdateEventBadRequest() throws Exception {
        when(eventService.updateEvent(eq(1L), any(EventDTO.class)))
                .thenThrow(new IllegalArgumentException("Event name must not be blank."));

        mockMvc.perform(put("/api/events/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleEvent)))
                .andExpect(status().isBadRequest());
    }

    // ── DELETE /api/events/{id} ───────────────────────────────────────────────

    @Test
    @WithMockUser
    @DisplayName("DELETE /api/events/1 - should return 204 when deleted")
    void testDeleteEventSuccess() throws Exception {
        when(eventService.deleteEvent(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/events/1").with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser
    @DisplayName("DELETE /api/events/99 - should return 404 when not found")
    void testDeleteEventNotFound() throws Exception {
        when(eventService.deleteEvent(99L)).thenReturn(false);

        mockMvc.perform(delete("/api/events/99").with(csrf()))
                .andExpect(status().isNotFound());
    }
}