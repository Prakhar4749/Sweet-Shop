package com.sweetShop.Backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * The Sweet Entity.
 * This class represents a single row in our database table 'sweets'.
 * It maps the Java code directly to the PostgreSQL database.
 */
@Entity // Tells Hibernate: "This is a database object, please create a table for it."
@Table(name = "sweets") // Explicitly names the database table "sweets".
@Data // Lombok: Automatically generates Getters, Setters, Equals, HashCode, etc.
@NoArgsConstructor // Required by JPA to create empty objects before filling data.
@AllArgsConstructor // Helpful for testing (allows new Sweet(1, "Name", ...)).
public class Sweet {

    @Id // Marks this field as the Primary Key (Unique ID).
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Tells DB: "You auto-generate this number (1, 2, 3...)."
    private Long id;

    // Validation: Ensures we never save a sweet with a blank name (e.g., "").
    @NotBlank(message = "Name cannot be empty")
    // Database Rule: The name column cannot be null, and NO duplicates allowed.
    @Column(nullable = false, unique = true)
    private String name;

    @NotBlank(message = "Category cannot be empty")
    private String category;

    // Validation: Ensures price is provided.
    @NotNull(message = "Price is required")
    // Validation: Ensures price is 0 or higher (No negative money!).
    @Min(value = 0, message = "Price cannot be negative")
    private Double price;

    // Validation: Ensures quantity is 0 or higher.
    @Min(value = 0, message = "Quantity cannot be negative")
    private Integer quantity;
}