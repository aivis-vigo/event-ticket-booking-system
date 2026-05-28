package com.example.event_ticket_booking_system.repository;
import com.example.event_ticket_booking_system.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.event_ticket_booking_system.entity.Booking.BookingStatus;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByCustomerEmailIgnoreCase(String customerEmail);

    List<Booking> findByStatus(BookingStatus status);

    List<Booking> findByEventId(Long eventId);

    boolean existsByTicketIdAndStatus(Long ticketId, BookingStatus status);
}