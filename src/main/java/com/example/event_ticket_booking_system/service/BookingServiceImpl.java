package com.example.event_ticket_booking_system.service;
import com.example.event_ticket_booking_system.dto.BookingDTO;
import com.example.event_ticket_booking_system.entity.Booking;
import com.example.event_ticket_booking_system.entity.Ticket;
import com.example.event_ticket_booking_system.repository.BookingRepository;
import com.example.event_ticket_booking_system.repository.TicketRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final TicketRepository ticketRepository;

    public BookingServiceImpl(BookingRepository bookingRepository, TicketRepository ticketRepository) {
        this.bookingRepository = bookingRepository;
        this.ticketRepository = ticketRepository;
    }

    @Override
    @Transactional
    public BookingDTO createBooking(BookingDTO bookingDTO) {
        validateCustomerFields(bookingDTO);
        if (bookingDTO.getTicketId() == null) {
            throw new IllegalArgumentException("Ticket id is required.");
        }
        Ticket ticket = findTicketOrThrow(bookingDTO.getTicketId());
        if (!ticket.isAvailable()) {
            throw new IllegalStateException("Ticket is not available for booking.");
        }
        ticket.book();
        ticketRepository.save(ticket);
        Booking booking = new Booking(ticket, ticket.getEvent(), bookingDTO.getCustomerName().trim(), bookingDTO.getCustomerEmail().trim(), LocalDateTime.now());
        booking.setStatus(Booking.BookingStatus.CONFIRMED);
        Booking savedBooking = bookingRepository.save(booking);
        return toDTO(savedBooking);
    }

    @Override
    public BookingDTO getBookingById(Long id) {
        return toDTO(findBookingOrThrow(id));
    }

    @Override
    public List<BookingDTO> getAllBookings() {
        return bookingRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public List<BookingDTO> getBookingsByEvent(Long eventId) {
        return bookingRepository.findByEventId(eventId).stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public List<BookingDTO> getBookingsForCustomer(String customerEmail) {
        if (customerEmail == null || customerEmail.isBlank()) {
            throw new IllegalArgumentException("Customer email is required.");
        }
        return bookingRepository.findByCustomerEmailIgnoreCase(customerEmail.trim()).stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public BookingDTO updateBooking(Long id, BookingDTO bookingDTO) {
        validateCustomerFields(bookingDTO);
        Booking booking = findBookingOrThrow(id);
        booking.setCustomerName(bookingDTO.getCustomerName().trim());
        booking.setCustomerEmail(bookingDTO.getCustomerEmail().trim());
        return toDTO(bookingRepository.save(booking));
    }

    @Override
    @Transactional
    public BookingDTO cancelBooking(Long id) {
        Booking booking = findBookingOrThrow(id);
        if (booking.getStatus() == Booking.BookingStatus.CANCELLED) {
            throw new IllegalStateException("Booking is already cancelled.");
        }
        booking.setStatus(Booking.BookingStatus.CANCELLED);
        Ticket ticket = booking.getTicket();
        if (ticket != null) {
            ticket.setStatus(Ticket.TicketStatus.AVAILABLE);
            ticketRepository.save(ticket);
        }
        return toDTO(bookingRepository.save(booking));
    }

    @Override
    @Transactional
    public void deleteBooking(Long id) {
        Booking booking = findBookingOrThrow(id);
        bookingRepository.delete(booking);
    }

    private Booking findBookingOrThrow(Long id) {
        return bookingRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Booking not found with id: " + id));
    }

    private Ticket findTicketOrThrow(Long id) {
        return ticketRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Ticket not found with id: " + id));
    }

    private void validateCustomerFields(BookingDTO dto) {
        if (dto.getCustomerName() == null || dto.getCustomerName().isBlank()) {
            throw new IllegalArgumentException("Customer name must not be blank.");
        }
        if (dto.getCustomerEmail() == null || dto.getCustomerEmail().isBlank()) {
            throw new IllegalArgumentException("Customer email must not be blank.");
        }
    }

    private BookingDTO toDTO(Booking booking) {
        BookingDTO dto = new BookingDTO();
        dto.setId(booking.getId());
        if (booking.getTicket() != null) {
            dto.setTicketId(booking.getTicket().getId());
            dto.setTicketNumber(booking.getTicket().getTicketNumber());
        }
        if (booking.getEvent() != null) {
            dto.setEventId(booking.getEvent().getId());
            dto.setEventName(booking.getEvent().getName());
        }
        dto.setCustomerName(booking.getCustomerName());
        dto.setCustomerEmail(booking.getCustomerEmail());
        dto.setBookingDate(booking.getBookingDate());
        dto.setStatus(booking.getStatus());
        return dto;
    }
}
