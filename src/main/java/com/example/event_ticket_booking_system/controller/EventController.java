package com.example.event_ticket_booking_system.controller;

import com.example.event_ticket_booking_system.dto.EventDTO;
import com.example.event_ticket_booking_system.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events")
public class EventController {

    @Autowired
    private EventService eventService;

    // Optional ?keyword= query parameter for search
    @GetMapping
    public List<EventDTO> getAllEvents(@RequestParam(required = false) String keyword) {
        if (keyword != null && !keyword.isBlank()) {
            return eventService.searchEvents(keyword);
        }
        return eventService.getAllEvents();
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventDTO> getEventById(@PathVariable Long id) {
        return eventService.getEventById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/upcoming")
    public List<EventDTO> getUpcomingEvents() {
        return eventService.getUpcomingEvents();
    }

    // GET /api/events/search?location=&minPrice=&maxPrice=
    @GetMapping("/search")
    public List<EventDTO> searchEvents(@RequestParam(required = false) String location, @RequestParam(required = false) Double minPrice, @RequestParam(required = false) Double maxPrice) {

        if (location != null && !location.isBlank()) {
            return eventService.getEventsByLocation(location);
        }
        if (minPrice != null && maxPrice != null) {
            return eventService.getEventsByPriceRange(minPrice, maxPrice);
        }
        return eventService.getAllEvents();
    }

    // POST /api/events
    @PostMapping
    public ResponseEntity<?> createEvent(@RequestBody EventDTO eventDTO) {
        try {
            EventDTO created = eventService.createEvent(eventDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    // PUT /api/events/{id}
    @PutMapping("/{id}")
    public ResponseEntity<?> updateEvent(@PathVariable Long id, @RequestBody EventDTO eventDTO) {
        try {
            return eventService.updateEvent(id, eventDTO).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    // DELETE /api/events/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
        return eventService.deleteEvent(id) ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}

