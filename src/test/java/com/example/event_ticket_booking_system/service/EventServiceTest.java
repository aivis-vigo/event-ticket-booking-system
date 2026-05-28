package com.example.event_ticket_booking_system.service;

import com.example.event_ticket_booking_system.dto.EventDTO;
import com.example.event_ticket_booking_system.entity.Event;
import com.example.event_ticket_booking_system.repository.EventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("EventService Tests")
class EventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private EventService eventService;

    private Event sampleEvent;
    private EventDTO sampleDTO;
    private static final LocalDateTime FUTURE_DATE = LocalDateTime.now().plusDays(30);

    @BeforeEach
    void setUp() {
        sampleEvent = new Event("Java Conference", "Annual Java event",
                FUTURE_DATE, "San Francisco", 500, 99.99);
        sampleEvent.setId(1L);

        sampleDTO = new EventDTO(null, "Java Conference", "Annual Java event",
                FUTURE_DATE, "San Francisco", 500, 99.99);
    }

    // ── getAllEvents ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("Should return all events as DTOs")
    void testGetAllEvents() {
        when(eventRepository.findAll()).thenReturn(List.of(sampleEvent));

        List<EventDTO> result = eventService.getAllEvents();

        assertEquals(1, result.size());
        assertEquals("Java Conference", result.get(0).getName());
    }

    @Test
    @DisplayName("Should return empty list when no events exist")
    void testGetAllEventsEmpty() {
        when(eventRepository.findAll()).thenReturn(List.of());

        List<EventDTO> result = eventService.getAllEvents();

        assertTrue(result.isEmpty());
    }

    // ── getEventById ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("Should return event when found by id")
    void testGetEventByIdFound() {
        when(eventRepository.findById(1L)).thenReturn(Optional.of(sampleEvent));

        Optional<EventDTO> result = eventService.getEventById(1L);

        assertTrue(result.isPresent());
        assertEquals("Java Conference", result.get().getName());
        assertEquals(1L, result.get().getId());
    }

    @Test
    @DisplayName("Should return empty Optional when event not found")
    void testGetEventByIdNotFound() {
        when(eventRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<EventDTO> result = eventService.getEventById(99L);

        assertFalse(result.isPresent());
    }

    // ── searchEvents ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("Should return all events when keyword is blank")
    void testSearchEventsBlankKeyword() {
        when(eventRepository.findAll()).thenReturn(List.of(sampleEvent));

        List<EventDTO> result = eventService.searchEvents("  ");

        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("Should return all events when keyword is null")
    void testSearchEventsNullKeyword() {
        when(eventRepository.findAll()).thenReturn(List.of(sampleEvent));

        List<EventDTO> result = eventService.searchEvents(null);

        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("Should filter events by keyword matching name")
    void testSearchEventsByNameKeyword() {
        Event other = new Event("Spring Boot Workshop", "Workshop desc",
                FUTURE_DATE, "New York", 100, 49.99);
        when(eventRepository.findAll()).thenReturn(List.of(sampleEvent, other));

        List<EventDTO> result = eventService.searchEvents("java");

        assertEquals(1, result.size());
        assertEquals("Java Conference", result.get(0).getName());
    }

    @Test
    @DisplayName("Should filter events by keyword matching description")
    void testSearchEventsByDescriptionKeyword() {
        when(eventRepository.findAll()).thenReturn(List.of(sampleEvent));

        List<EventDTO> result = eventService.searchEvents("annual");

        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("Should filter events by keyword matching location")
    void testSearchEventsByLocationKeyword() {
        when(eventRepository.findAll()).thenReturn(List.of(sampleEvent));

        List<EventDTO> result = eventService.searchEvents("francisco");

        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("Should return empty list when keyword matches nothing")
    void testSearchEventsNoMatch() {
        when(eventRepository.findAll()).thenReturn(List.of(sampleEvent));

        List<EventDTO> result = eventService.searchEvents("xyzNotFound");

        assertTrue(result.isEmpty());
    }

    // ── getUpcomingEvents ─────────────────────────────────────────────────────

    @Test
    @DisplayName("Should return upcoming events")
    void testGetUpcomingEvents() {
        when(eventRepository.findByEventDateAfter(any(LocalDateTime.class)))
                .thenReturn(List.of(sampleEvent));

        List<EventDTO> result = eventService.getUpcomingEvents();

        assertEquals(1, result.size());
        verify(eventRepository).findByEventDateAfter(any(LocalDateTime.class));
    }

    // ── getEventsByLocation ───────────────────────────────────────────────────

    @Test
    @DisplayName("Should return events by location")
    void testGetEventsByLocation() {
        when(eventRepository.findByLocationContainingIgnoreCase("San Francisco"))
                .thenReturn(List.of(sampleEvent));

        List<EventDTO> result = eventService.getEventsByLocation("San Francisco");

        assertEquals(1, result.size());
        assertEquals("San Francisco", result.get(0).getLocation());
    }

    // ── getEventsByPriceRange ─────────────────────────────────────────────────

    @Test
    @DisplayName("Should return events within price range")
    void testGetEventsByPriceRange() {
        when(eventRepository.findByTicketPriceBetween(50.0, 150.0))
                .thenReturn(List.of(sampleEvent));

        List<EventDTO> result = eventService.getEventsByPriceRange(50.0, 150.0);

        assertEquals(1, result.size());
    }

    // ── createEvent ───────────────────────────────────────────────────────────

    @Test
    @DisplayName("Should create and return a new event")
    void testCreateEventSuccess() {
        when(eventRepository.save(any(Event.class))).thenAnswer(inv -> {
            Event e = inv.getArgument(0);
            e.setId(1L);
            return e;
        });

        EventDTO result = eventService.createEvent(sampleDTO);

        assertNotNull(result);
        assertEquals("Java Conference", result.getName());
        verify(eventRepository).save(any(Event.class));
    }

    @Test
    @DisplayName("Should reject event with blank name")
    void testCreateEventBlankName() {
        sampleDTO.setName("  ");

        assertThrows(IllegalArgumentException.class, () -> eventService.createEvent(sampleDTO));
        verify(eventRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should reject event with null name")
    void testCreateEventNullName() {
        sampleDTO.setName(null);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> eventService.createEvent(sampleDTO));
        assertEquals("Event name must not be blank.", ex.getMessage());
    }

    @Test
    @DisplayName("Should reject event with blank description")
    void testCreateEventBlankDescription() {
        sampleDTO.setDescription("");

        assertThrows(IllegalArgumentException.class, () -> eventService.createEvent(sampleDTO));
    }

    @Test
    @DisplayName("Should reject event with null date")
    void testCreateEventNullDate() {
        sampleDTO.setEventDate(null);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> eventService.createEvent(sampleDTO));
        assertEquals("Event date must not be null.", ex.getMessage());
    }

    @Test
    @DisplayName("Should reject event with past date")
    void testCreateEventPastDate() {
        sampleDTO.setEventDate(LocalDateTime.now().minusDays(1));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> eventService.createEvent(sampleDTO));
        assertEquals("Event date must be in the future.", ex.getMessage());
    }

    @Test
    @DisplayName("Should reject event with blank location")
    void testCreateEventBlankLocation() {
        sampleDTO.setLocation("  ");

        assertThrows(IllegalArgumentException.class, () -> eventService.createEvent(sampleDTO));
    }

    @Test
    @DisplayName("Should reject event with zero total tickets")
    void testCreateEventZeroTickets() {
        sampleDTO.setTotalTickets(0);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> eventService.createEvent(sampleDTO));
        assertEquals("Total tickets must be a positive number.", ex.getMessage());
    }

    @Test
    @DisplayName("Should reject event with negative total tickets")
    void testCreateEventNegativeTickets() {
        sampleDTO.setTotalTickets(-5);

        assertThrows(IllegalArgumentException.class, () -> eventService.createEvent(sampleDTO));
    }

    @Test
    @DisplayName("Should reject event with negative ticket price")
    void testCreateEventNegativePrice() {
        sampleDTO.setTicketPrice(-1.0);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> eventService.createEvent(sampleDTO));
        assertEquals("Ticket price must be zero or greater.", ex.getMessage());
    }

    @Test
    @DisplayName("Should allow event with zero ticket price")
    void testCreateEventZeroPrice() {
        sampleDTO.setTicketPrice(0.0);
        when(eventRepository.save(any(Event.class))).thenAnswer(inv -> inv.getArgument(0));

        EventDTO result = eventService.createEvent(sampleDTO);

        assertEquals(0.0, result.getTicketPrice());
    }

    // ── updateEvent ───────────────────────────────────────────────────────────

    @Test
    @DisplayName("Should update an existing event")
    void testUpdateEventSuccess() {
        when(eventRepository.findById(1L)).thenReturn(Optional.of(sampleEvent));
        when(eventRepository.save(any(Event.class))).thenAnswer(inv -> inv.getArgument(0));

        EventDTO update = new EventDTO(null, "Updated Name", "Updated Desc",
                FUTURE_DATE, "Los Angeles", 200, 79.99);

        Optional<EventDTO> result = eventService.updateEvent(1L, update);

        assertTrue(result.isPresent());
        assertEquals("Updated Name", result.get().getName());
        assertEquals("Los Angeles", result.get().getLocation());
        verify(eventRepository).save(any(Event.class));
    }

    @Test
    @DisplayName("Should return empty Optional when updating non-existent event")
    void testUpdateEventNotFound() {
        when(eventRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<EventDTO> result = eventService.updateEvent(99L, sampleDTO);

        assertFalse(result.isPresent());
        verify(eventRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should reject update with invalid data")
    void testUpdateEventInvalidData() {
        when(eventRepository.findById(1L)).thenReturn(Optional.of(sampleEvent));
        sampleDTO.setName("");

        assertThrows(IllegalArgumentException.class, () -> eventService.updateEvent(1L, sampleDTO));
        verify(eventRepository, never()).save(any());
    }

    // ── deleteEvent ───────────────────────────────────────────────────────────

    @Test
    @DisplayName("Should delete existing event and return true")
    void testDeleteEventSuccess() {
        when(eventRepository.existsById(1L)).thenReturn(true);

        boolean result = eventService.deleteEvent(1L);

        assertTrue(result);
        verify(eventRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Should return false when deleting non-existent event")
    void testDeleteEventNotFound() {
        when(eventRepository.existsById(99L)).thenReturn(false);

        boolean result = eventService.deleteEvent(99L);

        assertFalse(result);
        verify(eventRepository, never()).deleteById(any());
    }

    // ── toDTO mapping ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("Should map all fields correctly in toDTO")
    void testToDTOMapping() {
        EventDTO dto = eventService.toDTO(sampleEvent);

        assertEquals(1L, dto.getId());
        assertEquals("Java Conference", dto.getName());
        assertEquals("Annual Java event", dto.getDescription());
        assertEquals(FUTURE_DATE, dto.getEventDate());
        assertEquals("San Francisco", dto.getLocation());
        assertEquals(500, dto.getTotalTickets());
        assertEquals(99.99, dto.getTicketPrice());
    }
}