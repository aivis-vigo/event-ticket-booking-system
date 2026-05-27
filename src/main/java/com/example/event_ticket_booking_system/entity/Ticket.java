package com.example.event_ticket_booking_system.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "tickets")
public class Ticket extends BaseEntity {

    /*
     * INHERITANCE:
     * Ticket extends BaseEntity, so it inherits:
     * - id
     * - getId()
     * - setId()
     */

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @Column(nullable = false, unique = true)
    private String ticketNumber;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TicketStatus status;

    @Column(nullable = false)
    private Double price;

    /*
     * ENUM:
     * TicketStatus allows only predefined ticket states.
     */
    public enum TicketStatus {
        AVAILABLE,
        RESERVED,
        BOOKED,
        CANCELLED
    }

    public Ticket() {
    }

    public Ticket(Event event, String ticketNumber, Double price) {
        this.event = event;
        this.ticketNumber = ticketNumber;
        this.price = price;
        this.status = TicketStatus.AVAILABLE;
    }

    /*
     * BUSINESS LOGIC:
     * Checks if the ticket can be reserved or booked.
     */
    public boolean isAvailable() {
        return this.status == TicketStatus.AVAILABLE;
    }

    /*
     * BUSINESS LOGIC:
     * Changes status to RESERVED only if ticket is AVAILABLE.
     */
    public void reserve() {
        if (!isAvailable()) {
            throw new IllegalStateException("Only available tickets can be reserved.");
        }
        this.status = TicketStatus.RESERVED;
    }

    /*
     * BUSINESS LOGIC:
     * Changes status to BOOKED only if ticket is AVAILABLE or RESERVED.
     */
    public void book() {
        if (this.status != TicketStatus.AVAILABLE && this.status != TicketStatus.RESERVED) {
            throw new IllegalStateException("Only available or reserved tickets can be booked.");
        }
        this.status = TicketStatus.BOOKED;
    }

    /*
     * BUSINESS LOGIC:
     * Changes status to CANCELLED.
     */
    public void cancel() {
        this.status = TicketStatus.CANCELLED;
    }

    /*
     * ENCAPSULATION:
     * Fields are private, access is through getters and setters.
     */

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public String getTicketNumber() {
        return ticketNumber;
    }

    public void setTicketNumber(String ticketNumber) {
        this.ticketNumber = ticketNumber;
    }

    public TicketStatus getStatus() {
        return status;
    }

    public void setStatus(TicketStatus status) {
        this.status = status;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }
}