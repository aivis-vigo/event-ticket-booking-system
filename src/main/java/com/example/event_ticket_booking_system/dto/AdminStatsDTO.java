package com.example.event_ticket_booking_system.dto;

public class AdminStatsDTO {
    private long totalEvents;
    private long totalTickets;
    private long availableTickets;
    private long bookedTickets;
    private long totalBookings;

    public AdminStatsDTO(long totalEvents, long totalTickets, long availableTickets, long bookedTickets, long totalBookings) {
        this.totalEvents = totalEvents;
        this.totalTickets = totalTickets;
        this.availableTickets = availableTickets;
        this.bookedTickets = bookedTickets;
        this.totalBookings = totalBookings;
    }

    public long getTotalEvents() {
        return totalEvents;
    }

    public long getTotalTickets() {
        return totalTickets;
    }

    public long getAvailableTickets() {
        return availableTickets;
    }

    public long getBookedTickets() {
        return bookedTickets;
    }

    public long getTotalBookings() {
        return totalBookings;
    }
}