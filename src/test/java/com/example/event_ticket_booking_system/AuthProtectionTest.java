package com.example.event_ticket_booking_system;

import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.HttpStatus;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AuthProtectionTest {

    @Test
    void protectedEndpointsRequireAuth() throws Exception {
        try (ConfigurableApplicationContext context = new SpringApplication(EventTicketBookingSystemApplication.class)
                .run("--server.port=0", "--spring.main.web-application-type=servlet")) {

                  int port = Integer.parseInt(Objects.requireNonNull(context.getEnvironment().getProperty("local.server.port")));
            HttpClient client = HttpClient.newHttpClient();

                  assertEquals(HttpStatus.UNAUTHORIZED.value(), get(client, port, "/api/events", null));
                  assertEquals(HttpStatus.OK.value(), get(client, port, "/api/events", defaultUserAuth()));

                  assertEquals(HttpStatus.UNAUTHORIZED.value(), get(client, port, "/api/tickets", null));
                  assertEquals(HttpStatus.OK.value(), get(client, port, "/api/tickets", defaultUserAuth()));

                  assertEquals(HttpStatus.UNAUTHORIZED.value(), get(client, port, "/api/bookings", null));
                  assertEquals(HttpStatus.OK.value(), get(client, port, "/api/bookings", defaultUserAuth()));
        }
    }

    private static int get(HttpClient client, int port, String path, String authorizationHeader) throws Exception {
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + path))
                .GET();

        if (authorizationHeader != null) {
            requestBuilder.header("Authorization", authorizationHeader);
        }

        return client.send(requestBuilder.build(), HttpResponse.BodyHandlers.ofString()).statusCode();
    }

      private static String defaultUserAuth() {
        String token = "user@example.com:user123";
        return "Basic " + Base64.getEncoder().encodeToString(token.getBytes());
    }
}

