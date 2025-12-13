package com.sweetShop.Backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * AuthRequestDto (Data Transfer Object).
 * This class is just a simple "Container" or "Box" for holding data.
 * It represents the form the user fills out when they want to Register or Login.
 */
@Data // Lombok: Automatically writes the Getters, Setters, and toString() for us.
@AllArgsConstructor // Lombok: Creates a constructor that accepts all fields.
@NoArgsConstructor // Lombok: Creates an empty constructor (needed by JSON tools).
public class AuthRequestDto {

    // The name they want to use (e.g., "johndoe").
    private String username;

    // The secret password they want to set (e.g., "secret123").
    private String password;

    // The "Backdoor" key.
    // If the user sends this specific text field with the correct secret code,
    // our logic (in AuthService) will make them an ADMIN.
    // Ordinary users will leave this blank or null.
    private String adminKey;
}