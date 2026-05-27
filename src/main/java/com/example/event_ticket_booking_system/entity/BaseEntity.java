package com.example.event_ticket_booking_system.entity;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;

/*
 * INHERITANCE:
 * This abstract parent class stores common entity fields.
 * Ticket can inherit id from BaseEntity.
 */
@MappedSuperclass
public abstract class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /*
     * ENCAPSULATION:
     * id is private, access is controlled through getter and setter.
     */
    public Long getId() {
        return id;
    }

    /*
     * Setter is kept for compatibility with tests or data initialization.
     */
    public void setId(Long id) {
        this.id = id;
    }
}