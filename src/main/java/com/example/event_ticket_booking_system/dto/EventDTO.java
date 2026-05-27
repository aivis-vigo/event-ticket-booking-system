package com.example.event_ticket_booking_system.dto;

import java.time.LocalDateTime;

public class EventDTO {

    private Long id;
    private String name;
    private String description;
    private LocalDateTime eventDate;
    private String location;
    private Integer totalTickets;
    private Double ticketPrice;

    // ── Constructors ─────────────────────────────────────────────────────────
    public EventDTO() {
    }

    public EventDTO(Long id, String name, String description, LocalDateTime eventDate, String location, Integer totalTickets, Double ticketPrice) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.eventDate = eventDate;
        this.location = location;
        this.totalTickets = totalTickets;
        this.ticketPrice = ticketPrice;
    }

    // ── Getters & Setters ────────────────────────────────────────────────────
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getEventDate() {
        return eventDate;
    }

    public void setEventDate(LocalDateTime eventDate) {
        this.eventDate = eventDate;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Integer getTotalTickets() {
        return totalTickets;
    }

    public void setTotalTickets(Integer totalTickets) {
        this.totalTickets = totalTickets;
    }

    public Double getTicketPrice() {
        return ticketPrice;
    }

    public void setTicketPrice(Double ticketPrice) {
        this.ticketPrice = ticketPrice;
    }
}
