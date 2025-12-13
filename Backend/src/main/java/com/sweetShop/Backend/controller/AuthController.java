package com.sweetShop.Backend.controller;

import com.sweetShop.Backend.dto.AuthRequestDto;
import com.sweetShop.Backend.dto.AuthResponseDto;
import com.sweetShop.Backend.service.AuthService;
import com.sweetShop.Backend.util.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * The Authentication Controller.
 * This class handles the public-facing "front door" API endpoints.
 * It is responsible for onboarding new users and verifying existing ones.
 */
@RestController // Marks this class as a request handler (a "Receptionist").
@RequestMapping("/api/auth") // All URLs starting with "/api/auth" come to this desk.
@RequiredArgsConstructor
public class AuthController {

    // The backend service that does the heavy lifting (checking database, hashing passwords).
    private final AuthService authService;

    /**
     * Endpoint: POST /api/auth/register
     * Purpose: Sign up a new user.
     * @param dto The raw data sent by the user (username, password).
     * @return A standard success message wrapped in JSON.
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<String>> register(@RequestBody AuthRequestDto dto) {

        // 1. Pass the form data to the service layer.
        // The service checks if the user exists, encrypts the password, and saves them.
        String result = authService.register(dto);

        // 2. Return a "201 Created" status code (Standard for creating new things).
        // We wrap the simple text result in our 'ApiResponse' so the frontend gets a consistent format:
        // { "success": true, "message": "User registered successfully", "data": null }
        return new ResponseEntity<>(new ApiResponse<>(true, result, null), HttpStatus.CREATED);
    }

    /**
     * Endpoint: POST /api/auth/login
     * Purpose: Log in an existing user and give them an access token.
     * @param dto The login credentials (username, password).
     * @return The JWT Token wrapped in JSON.
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponseDto>> login(@RequestBody AuthRequestDto dto) {

        // 1. Ask the service to verify the credentials.
        // If correct, the service generates and returns a JWT Token (ID Card).
        // If incorrect, the service will throw an error (handled by GlobalExceptionHandler).
        AuthResponseDto response = authService.login(dto);

        // 2. Return a "200 OK" status.
        // Send the token back inside the data field:
        // { "success": true, "message": "Login Successful", "data": { "token": "eyJh..." } }
        return ResponseEntity.ok(new ApiResponse<>(true, "Login Successful", response));
    }
}