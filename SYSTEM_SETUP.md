# Event Ticket Booking System - Starting Point

A minimalistic Spring Boot application providing a foundation for building a ticket booking system with REST APIs for managing events, tickets, and bookings.

## Architecture Overview

### Entities
- **Event**: Represents an event with details like name, description, date, location, ticket count, and price
- **Ticket**: Represents individual tickets for events with status (AVAILABLE/BOOKED)
- **Booking**: Records customer bookings with confirmation status

### Project Structure

```
src/main/java/com/example/event_ticket_booking_system/
├── entity/
│   ├── Event.java          # Event entity
│   ├── Ticket.java         # Ticket entity
│   └── Booking.java        # Booking entity
├── repository/
│   ├── EventRepository.java      # Event data access
│   ├── TicketRepository.java     # Ticket data access
│   └── BookingRepository.java    # Booking data access
├── controller/
│   ├── EventController.java      # Event REST endpoints
│   ├── TicketController.java     # Ticket REST endpoints
│   └── BookingController.java    # Booking REST endpoints
├── config/
│   └── DataLoader.java     # Initial database data
└── EventTicketBookingSystemApplication.java
```

## REST API Endpoints

### Events
- `GET /api/events` - Get all events
- `GET /api/events/{id}` - Get event by ID
- `POST /api/events` - Create new event
- `PUT /api/events/{id}` - Update event
- `DELETE /api/events/{id}` - Delete event

### Tickets
- `GET /api/tickets` - Get all tickets
- `GET /api/tickets/{id}` - Get ticket by ID
- `POST /api/tickets` - Create new ticket
- `PUT /api/tickets/{id}` - Update ticket
- `DELETE /api/tickets/{id}` - Delete ticket

### Bookings
- `GET /api/bookings` - Get all bookings
- `GET /api/bookings/{id}` - Get booking by ID
- `POST /api/bookings` - Create new booking
- `PUT /api/bookings/{id}` - Update booking
- `DELETE /api/bookings/{id}` - Delete booking

## Sample Data

The application comes pre-loaded with sample data:

### Events:
1. **Spring Boot Workshop** (100 tickets @ $49.99) - June 15, 2026
2. **Java Conference 2026** (500 tickets @ $99.99) - July 20, 2026
3. **Web Development Bootcamp** (75 tickets @ $79.99) - August 10, 2026

### Bookings:
- John Doe booked a Spring Boot Workshop ticket
- Jane Smith booked a Spring Boot Workshop ticket

## Database

- **Type**: H2 (in-memory)
- **Console**: Available at `http://localhost:8080/h2-console`
- **URL**: `jdbc:h2:mem:ticketdb`
- **Username**: `sa`
- **Password**: (empty)

## Building and Running

### Build
```bash
./mvnw clean install
```

### Run
```bash
java -jar target/event-ticket-booking-system-0.0.1-SNAPSHOT.jar
```

The application will start on `http://localhost:8080`

## Example API Calls

### Get All Events
```bash
curl -X GET http://localhost:8080/api/events
```

### Get All Tickets
```bash
curl -X GET http://localhost:8080/api/tickets
```

### Get All Bookings
```bash
curl -X GET http://localhost:8080/api/bookings
```

### Create New Event
```bash
curl -X POST http://localhost:8080/api/events \
  -H "Content-Type: application/json" \
  -d '{
    "name": "DevOps Workshop",
    "description": "Learn Docker and Kubernetes",
    "eventDate": "2026-09-10T10:00:00",
    "location": "Seattle Convention Center",
    "totalTickets": 50,
    "ticketPrice": 89.99
  }'
```

## Technology Stack

- **Framework**: Spring Boot 4.0.6
- **Language**: Java 21
- **Database**: H2 (in-memory)
- **ORM**: Spring Data JPA
- **Build Tool**: Maven

## Next Steps for Development

1. Add service layer for business logic
2. Implement validation and error handling
3. Add authentication/authorization
4. Create a frontend UI
5. Add more complex queries (filter events by date, search, etc.)
6. Implement payment processing
7. Add email notifications
8. Create comprehensive test suite
9. Add API documentation (Swagger/OpenAPI)
10. Implement transaction management for bookings

