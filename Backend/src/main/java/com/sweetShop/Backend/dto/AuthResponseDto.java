package com.sweetShop.Backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * AuthResponseDto (Data Transfer Object).
 * This class is the "Package" we send back to the user after a successful login.
 * It contains the proof of their identity.
 */
@Data // Automatically creates Getters/Setters so we can access the data.
@AllArgsConstructor // Automatically creates a constructor so we can easily fill the data.
public class AuthResponseDto {

    // This string holds the actual JWT (JSON Web Token).
    // It looks like a long specific code (e.g., "eyJhbGciOi...").
    // The frontend (React app) will receive this and save it,
    // attaching it to every future request as their "Digital ID Card."
    private String token;
}