package com.example.event_ticket_booking_system.dto;

import com.example.event_ticket_booking_system.entity.Ticket;

/*
 * Data Transfer Object.
 * DTO is used so the API does not expose the full Ticket entity directly.
 * This prevents JSON recursion problems with Event -> Ticket -> Event.
 */
public class TicketDTO {

    private Long id;
    private Long eventId;
    private String eventName;
    private String ticketNumber;
    private Ticket.TicketStatus status;
    private Double price;
    private Boolean available;

    public TicketDTO() {
    }

    public TicketDTO(Long id, Long eventId, String eventName, String ticketNumber,
                     Ticket.TicketStatus status, Double price, Boolean available) {
        this.id = id;
        this.eventId = eventId;
        this.eventName = eventName;
        this.ticketNumber = ticketNumber;
        this.status = status;
        this.price = price;
        this.available = available;
    }

    public Long getId() {
        return id;
    }

    public Long getEventId() {
        return eventId;
    }

    public String getEventName() {
        return eventName;
    }

    public String getTicketNumber() {
        return ticketNumber;
    }

    public Ticket.TicketStatus getStatus() {
        return status;
    }

    public Double getPrice() {
        return price;
    }

    public Boolean getAvailable() {
        return available;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public void setTicketNumber(String ticketNumber) {
        this.ticketNumber = ticketNumber;
    }

    public void setStatus(Ticket.TicketStatus status) {
        this.status = status;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public void setAvailable(Boolean available) {
        this.available = available;
    }
}