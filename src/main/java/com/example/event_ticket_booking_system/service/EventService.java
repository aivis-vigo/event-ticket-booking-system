package com.example.event_ticket_booking_system.service;

import com.example.event_ticket_booking_system.dto.EventDTO;
import com.example.event_ticket_booking_system.entity.Event;
import com.example.event_ticket_booking_system.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;

    // ── Fetch all ────────────────────────────────────────────────────────────
    public List<EventDTO> getAllEvents() {
        return eventRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    // ── Fetch by ID ──────────────────────────────────────────────────────────
    public Optional<EventDTO> getEventById(Long id) {
        return eventRepository.findById(id).map(this::toDTO);
    }

    // ── Search / filter ──────────────────────────────────────────────────────
    public List<EventDTO> searchEvents(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return getAllEvents();
        }
        String lc = keyword.toLowerCase();
        return eventRepository.findAll().stream().filter(e -> e.getName().toLowerCase().contains(lc) || e.getDescription().toLowerCase().contains(lc) || e.getLocation().toLowerCase().contains(lc)).map(this::toDTO).collect(Collectors.toList());
    }

    public List<EventDTO> getUpcomingEvents() {
        return eventRepository.findByEventDateAfter(LocalDateTime.now()).stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<EventDTO> getEventsByLocation(String location) {
        return eventRepository.findByLocationContainingIgnoreCase(location).stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<EventDTO> getEventsByPriceRange(Double minPrice, Double maxPrice) {
        return eventRepository.findByTicketPriceBetween(minPrice, maxPrice).stream().map(this::toDTO).collect(Collectors.toList());
    }

    // ── Create ───────────────────────────────────────────────────────────────
    public EventDTO createEvent(EventDTO dto) {
        validateEvent(dto);
        Event event = toEntity(dto);
        return toDTO(eventRepository.save(event));
    }

    // ── Update ───────────────────────────────────────────────────────────────
    public Optional<EventDTO> updateEvent(Long id, EventDTO dto) {
        return eventRepository.findById(id).map(existing -> {
            validateEvent(dto);
            existing.setName(dto.getName());
            existing.setDescription(dto.getDescription());
            existing.setEventDate(dto.getEventDate());
            existing.setLocation(dto.getLocation());
            existing.setTotalTickets(dto.getTotalTickets());
            existing.setTicketPrice(dto.getTicketPrice());
            return toDTO(eventRepository.save(existing));
        });
    }

    // ── Delete ───────────────────────────────────────────────────────────────
    public boolean deleteEvent(Long id) {
        if (eventRepository.existsById(id)) {
            eventRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // ── Validation ───────────────────────────────────────────────────────────
    private void validateEvent(EventDTO dto) {
        if (dto.getName() == null || dto.getName().isBlank()) {
            throw new IllegalArgumentException("Event name must not be blank.");
        }
        if (dto.getDescription() == null || dto.getDescription().isBlank()) {
            throw new IllegalArgumentException("Event description must not be blank.");
        }
        if (dto.getEventDate() == null) {
            throw new IllegalArgumentException("Event date must not be null.");
        }
        if (dto.getEventDate().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Event date must be in the future.");
        }
        if (dto.getLocation() == null || dto.getLocation().isBlank()) {
            throw new IllegalArgumentException("Event location must not be blank.");
        }
        if (dto.getTotalTickets() == null || dto.getTotalTickets() <= 0) {
            throw new IllegalArgumentException("Total tickets must be a positive number.");
        }
        if (dto.getTicketPrice() == null || dto.getTicketPrice() < 0) {
            throw new IllegalArgumentException("Ticket price must be zero or greater.");
        }
    }

    // ── Mapping helpers ──────────────────────────────────────────────────────
    public EventDTO toDTO(Event event) {
        EventDTO dto = new EventDTO();
        dto.setId(event.getId());
        dto.setName(event.getName());
        dto.setDescription(event.getDescription());
        dto.setEventDate(event.getEventDate());
        dto.setLocation(event.getLocation());
        dto.setTotalTickets(event.getTotalTickets());
        dto.setTicketPrice(event.getTicketPrice());
        return dto;
    }

    private Event toEntity(EventDTO dto) {
        return new Event(dto.getName(), dto.getDescription(), dto.getEventDate(), dto.getLocation(), dto.getTotalTickets(), dto.getTicketPrice());
    }
}
