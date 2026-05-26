# Event Ticket Booking System

A Spring Boot application for managing event ticket bookings. This system provides RESTful APIs to handle event ticketing operations.

## Prerequisites

Before you get started, ensure you have the following installed on your machine:

- **Java 21** or later
- **Maven 3.6.0** or later
- A text editor or IDE (IntelliJ IDEA, VS Code, Eclipse, etc.)

## Getting Started

### 1. Clone or Open the Project

Navigate to the project directory:

```bash
cd event-ticket-booking-system
```

### 2. Build the Project

Build the project using Maven:

```bash
./mvnw clean install
```

Or if you have Maven installed globally:

```bash
mvn clean install
```

### 3. Run the Application

Start the Spring Boot application:

```bash
./mvnw spring-boot:run
```

Or with Maven installed globally:

```bash
mvn spring-boot:run
```

The application will start and listen on **http://localhost:8080**

### Alternative: Run the JAR File

First, build the project:

```bash
./mvnw clean package
```

Then run the generated JAR file:

```bash
java -jar target/event-ticket-booking-system-0.0.1-SNAPSHOT.jar
```

## Project Structure

```
event-ticket-booking-system/
├── src/
│   ├── main/
│   │   ├── java/com/example/event_ticket_booking_system/
│   │   │   └── EventTicketBookingSystemApplication.java    # Main Spring Boot application entry point
│   │   └── resources/
│   │       └── application.properties                        # Application configuration
│   └── test/
│       └── java/com/example/event_ticket_booking_system/
│           └── EventTicketBookingSystemApplicationTests.java # Unit tests
├── pom.xml                                                    # Maven configuration and dependencies
├── mvnw & mvnw.cmd                                            # Maven wrapper (Windows & Unix)
└── README.md                                                  # This file
```

## Key Directories to Know

| Directory | Purpose |
|-----------|---------|
| `src/main/java/com/example/event_ticket_booking_system/` | Main application source code |
| `src/main/resources/` | Configuration files (application.properties, etc.) |
| `src/test/java/` | Unit tests for the application |
| `target/` | Compiled classes and packaged JAR (generated) |
| `pom.xml` | Project dependencies and Maven configuration |

## Technology Stack

- **Java 21**
- **Spring Boot 4.0.6**
- **Spring Boot Starter Web** (for REST APIs)
- **Maven** (build tool)

## Configuration

The application configuration can be customized in:

```
src/main/resources/application.properties
```

### Current Configuration

- **Application Name:** event-ticket-booking-system
- **Default Port:** 8080 (configurable via `server.port` in application.properties)

## API Endpoints

API endpoints will be implemented in controller classes within:

```
src/main/java/com/example/event_ticket_booking_system/
```

Once endpoints are implemented, test them using:

- **Browser:** http://localhost:8080/
- **Postman or cURL:** For POST/PUT/DELETE requests
- **VS Code REST Client or similar tools**

## Development Workflow

### Adding a New Feature

1. Create a new controller/service class in `src/main/java/`
2. Implement your business logic
3. Add corresponding unit tests in `src/test/java/`
4. Run `./mvnw test` to verify tests pass
5. Build and run locally to test the application

### Running Tests

#### Execute All Unit Tests

Run all tests in the project:

```bash
./mvnw clean test
```

Or with Maven installed globally:

```bash
mvn clean test
```

#### Run Tests in Quiet Mode (Minimal Output)

Run all tests with fewer logs:

```bash
./mvnw clean test -q
```

#### Run Specific Test Classes

Run all entity tests:

```bash
./mvnw test -Dtest=EventTest,TicketTest,BookingTest
```

Run a specific test class:

```bash
./mvnw test -Dtest=EventTest
```

Or:

```bash
./mvnw test -Dtest=EventTicketBookingSystemApplicationTests
```

#### Run Tests with Detailed Output

For verbose test execution:

```bash
./mvnw test -X
```

#### Test Files Location

Unit tests are located in:

```
src/test/java/com/example/event_ticket_booking_system/
├── entity/
│   ├── EventTest.java           # Tests for Event entity
│   ├── TicketTest.java          # Tests for Ticket entity
│   └── BookingTest.java         # Tests for Booking entity
└── EventTicketBookingSystemApplicationTests.java
```

#### Test Coverage Summary

- **Entity Tests:** 35+ test cases covering all entities (Event, Ticket, Booking)
- **Coverage includes:** Getters/setters, constructors, relationships, status transitions, and edge cases

### Code Formatting

Clean and rebuild the project:

```bash
./mvnw clean compile
```

## Troubleshooting

### Port 8080 Already in Use

If port 8080 is already in use, you can change it by adding or modifying the following in `src/main/resources/application.properties`:

```properties
server.port=8081
```

Then restart the application.

### Java Version Mismatch

Ensure Java 21 is installed:

```bash
java -version
```

If you have multiple Java versions, set `JAVA_HOME` to point to Java 21.

### Maven Build Issues

Clear the Maven cache and rebuild:

```bash
./mvnw clean install -U
```

## Team Guidelines

- **Code Location:** Add controllers, services, and models in `src/main/java/com/example/event_ticket_booking_system/`
- **Tests:** Write unit tests in `src/test/java/com/example/event_ticket_booking_system/`
- **Configuration:** Update `src/main/resources/application.properties` for environment-specific settings
- **Dependencies:** Add new dependencies in `pom.xml` under the `<dependencies>` section

## Additional Resources

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Maven Official Documentation](https://maven.apache.org/)
- [Spring Framework Guide](https://spring.io/guides)

## Support

For issues or questions about the project setup, refer to the configuration files or consult with the team lead.

---

Happy coding! 🚀

