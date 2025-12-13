package com.sweetShop.Backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SweetRequestDto (Data Transfer Object).
 * This class acts as a container for the information needed to create or update a sweet.
 * It separates the data coming from the user (DTO) from the data stored in the database (Entity).
 */
@Data // Lombok: Generates Getters, Setters, toString, etc.
@AllArgsConstructor // Lombok: Constructor for all fields.
@NoArgsConstructor // Lombok: Empty constructor for JSON processing.
public class SweetRequestDto {

    // The name of the sweet (e.g., "Gulab Jamun").
    private String name;

    // The type or group it belongs to (e.g., "Syrup Based", "Dry").
    private String category;

    // How much one unit costs (e.g., 50.0).
    // We use 'Double' for decimal numbers (money).
    private Double price;

    // How many of these items we have in stock right now (e.g., 100).
    private Integer quantity;
}