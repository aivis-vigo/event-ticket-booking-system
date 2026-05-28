package com.example.event_ticket_booking_system.service;
import com.example.event_ticket_booking_system.dto.BookingDTO;
import java.util.List;

public interface BookingService {

    BookingDTO createBooking(BookingDTO bookingDTO);

    BookingDTO getBookingById(Long id);

    List<BookingDTO> getAllBookings();

    List<BookingDTO> getBookingsByEvent(Long eventId);

    List<BookingDTO> getBookingsForCustomer(String customerEmail);

    BookingDTO updateBooking(Long id, BookingDTO bookingDTO);

    BookingDTO cancelBooking(Long id);

    void deleteBooking(Long id);
}