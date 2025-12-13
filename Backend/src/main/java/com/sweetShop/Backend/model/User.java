package com.sweetShop.Backend.model;

import jakarta.persistence.*;
import lombok.*;

/**
 * The User Entity.
 * This class maps to the 'users' table in our PostgreSQL database.
 * It stores login credentials and permission levels for every person using the app.
 */
@Entity // Tells Hibernate: "This class represents a table in the database."
@Table(name = "users") // Name the table 'users'.
@Data // Lombok: Generates getters, setters, toString(), equals(), etc. automatically.
@NoArgsConstructor // Lombok: Required by JPA (database framework) to instantiate the object.
@AllArgsConstructor // Lombok: A convenience constructor to create a User with all fields at once.
public class User {

    /**
     * Enum: A specific list of allowed values.
     * We limit roles to ONLY 'ADMIN' or 'USER'.
     * This prevents typos like 'admmin' or invalid roles like 'SuperUser'.
     */
    public enum Role {
        ADMIN,
        USER
    }

    @Id // Marks this field as the Primary Key (Unique Identifier).
    @GeneratedValue(strategy = GenerationType.IDENTITY) // The database will auto-increment this (1, 2, 3...).
    private Long id;

    // Database Constraint: Usernames must be unique (no duplicates) and cannot be empty.
    @Column(unique = true, nullable = false)
    private String username;

    // This stores the SCRAMBLED (hashed) password, not the plain text one.
    // E.g., "$2a$10$dXJ3..." instead of "password123".
    @Column(nullable = false)
    private String password;

    // Tells JPA to save the role as a String ("ADMIN") in the database,
    // rather than a number (0 or 1). This makes the database easier to read manually.
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;
}