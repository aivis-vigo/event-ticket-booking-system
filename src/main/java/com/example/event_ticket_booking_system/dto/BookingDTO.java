package com.example.event_ticket_booking_system.dto;
import com.example.event_ticket_booking_system.entity.Booking;
import java.time.LocalDateTime;

public class BookingDTO {

    private Long id;
    private Long ticketId;
    private String ticketNumber;
    private Long eventId;
    private String eventName;
    private String customerName;
    private String customerEmail;
    private LocalDateTime bookingDate;
    private Booking.BookingStatus status;

    public BookingDTO() {
    }

    public BookingDTO(Long id, Long ticketId, String ticketNumber, Long eventId, String eventName, String customerName, String customerEmail, LocalDateTime bookingDate, Booking.BookingStatus status) {
        this.id = id;
        this.ticketId = ticketId;
        this.ticketNumber = ticketNumber;
        this.eventId = eventId;
        this.eventName = eventName;
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.bookingDate = bookingDate;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public Long getTicketId() {
        return ticketId;
    }

    public String getTicketNumber() {
        return ticketNumber;
    }

    public Long getEventId() {
        return eventId;
    }

    public String getEventName() {
        return eventName;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public LocalDateTime getBookingDate() {
        return bookingDate;
    }

    public Booking.BookingStatus getStatus() {
        return status;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTicketId(Long ticketId) {
        this.ticketId = ticketId;
    }

    public void setTicketNumber(String ticketNumber) {
        this.ticketNumber = ticketNumber;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public void setBookingDate(LocalDateTime bookingDate) {
        this.bookingDate = bookingDate;
    }

    public void setStatus(Booking.BookingStatus status) {
        this.status = status;
    }
}