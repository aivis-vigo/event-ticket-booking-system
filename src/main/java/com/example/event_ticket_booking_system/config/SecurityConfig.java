package com.example.event_ticket_booking_system.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) {
		return http
				.csrf(AbstractHttpConfigurer::disable)
				.headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))
				.authorizeHttpRequests(auth -> auth
						.requestMatchers("/", "/index.html", "/styles.css", "/script.js", "/**/*.css", "/**/*.js", "/h2-console/**").permitAll()
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/events/**", "/api/tickets/**", "/api/bookings/**").authenticated()
						.requestMatchers(HttpMethod.POST, "/api/events/**", "/api/tickets/**", "/api/bookings/**").authenticated()
						.requestMatchers(HttpMethod.PUT, "/api/events/**", "/api/tickets/**", "/api/bookings/**").authenticated()
						.requestMatchers(HttpMethod.DELETE, "/api/events/**", "/api/tickets/**", "/api/bookings/**").authenticated()
						.anyRequest().permitAll()
				)
				.httpBasic(Customizer.withDefaults())
				.build();
	}

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
		return new InMemoryUserDetailsManager(
				User.withUsername("admin")
						.password(passwordEncoder.encode("admin123"))
						.roles("ADMIN")
						.build(),
				User.withUsername("user@example.com")
						.password(passwordEncoder.encode("user123"))
						.roles("USER")
						.build()
		);
	}
}

