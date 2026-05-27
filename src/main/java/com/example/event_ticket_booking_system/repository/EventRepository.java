package com.example.event_ticket_booking_system.repository;

import com.example.event_ticket_booking_system.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    // Upcoming events
    List<Event> findByEventDateAfter(LocalDateTime dateTime);

    // Location filter
    List<Event> findByLocationContainingIgnoreCase(String location);

    // Price range filter
    List<Event> findByTicketPriceBetween(Double minPrice, Double maxPrice);

    // Full-text keyword search across name, description, location
    @Query("SELECT e FROM Event e WHERE " +
            "LOWER(e.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(e.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(e.location) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Event> searchByKeyword(@Param("keyword") String keyword);

    // Events ordered soonest-first
    List<Event> findAllByOrderByEventDateAsc();
}

