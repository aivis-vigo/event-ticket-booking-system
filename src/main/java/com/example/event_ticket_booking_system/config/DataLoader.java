package com.example.event_ticket_booking_system.config;

import com.example.event_ticket_booking_system.entity.Event;
import com.example.event_ticket_booking_system.entity.Ticket;
import com.example.event_ticket_booking_system.entity.Booking;
import com.example.event_ticket_booking_system.repository.EventRepository;
import com.example.event_ticket_booking_system.repository.TicketRepository;
import com.example.event_ticket_booking_system.repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Override
    public void run(String... args) throws Exception {
        // Create sample events
        Event event1 = new Event(
            "Spring Boot Workshop",
            "Learn Spring Boot from basics to advanced",
            LocalDateTime.of(2026, 6, 15, 10, 0),
            "New York Convention Center",
            100,
            49.99
        );

        Event event2 = new Event(
            "Java Conference 2026",
            "Annual conference for Java developers",
            LocalDateTime.of(2026, 7, 20, 9, 0),
            "San Francisco Tech Hall",
            500,
            99.99
        );

        Event event3 = new Event(
            "Web Development Bootcamp",
            "Complete web development training",
            LocalDateTime.of(2026, 8, 10, 14, 0),
            "Los Angeles Tech Park",
            75,
            79.99
        );

        eventRepository.save(event1);
        eventRepository.save(event2);
        eventRepository.save(event3);

        // Create sample tickets for event1
        for (int i = 1; i <= 10; i++) {
            Ticket ticket = new Ticket(event1, "WORKSHOP-" + String.format("%04d", i), 49.99);
            ticketRepository.save(ticket);
        }

        // Create sample tickets for event2
        for (int i = 1; i <= 15; i++) {
            Ticket ticket = new Ticket(event2, "CONF-" + String.format("%04d", i), 99.99);
            ticketRepository.save(ticket);
        }

        // Create sample tickets for event3
        for (int i = 1; i <= 8; i++) {
            Ticket ticket = new Ticket(event3, "BOOTCAMP-" + String.format("%04d", i), 79.99);
            ticketRepository.save(ticket);
        }

        // Create sample bookings
        Ticket ticket1 = ticketRepository.findById(1L).orElse(null);
        if (ticket1 != null) {
            Booking booking1 = new Booking(
                ticket1,
                event1,
                "John Doe",
                "john@example.com",
                LocalDateTime.now()
            );
            booking1.setStatus(Booking.BookingStatus.CONFIRMED);
            ticket1.setStatus(Ticket.TicketStatus.BOOKED);
            ticketRepository.save(ticket1);
            bookingRepository.save(booking1);
        }

        Ticket ticket2 = ticketRepository.findById(2L).orElse(null);
        if (ticket2 != null) {
            Booking booking2 = new Booking(
                ticket2,
                event1,
                "Jane Smith",
                "jane@example.com",
                LocalDateTime.now()
            );
            booking2.setStatus(Booking.BookingStatus.CONFIRMED);
            ticket2.setStatus(Ticket.TicketStatus.BOOKED);
            ticketRepository.save(ticket2);
            bookingRepository.save(booking2);
        }
    }
}

