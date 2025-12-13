package com.sweetShop.Backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sweetShop.Backend.config.JwtFilter;
import com.sweetShop.Backend.config.JwtUtil;
import com.sweetShop.Backend.dto.AuthRequestDto;
import com.sweetShop.Backend.repository.UserRepository;
import com.sweetShop.Backend.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Auth Controller Test.
 * This tests the "Reception Desk" (API Endpoints) in isolation.
 * We are verifying that the URL addresses work and the JSON responses are correct.
 */
@WebMvcTest(controllers = AuthController.class) // Focus ONLY on the AuthController. Ignore everything else.
@AutoConfigureMockMvc(addFilters = false) // IMPORTANT: Turn OFF the Security Guards (Filters).
// We do this because we just want to test if the Controller logic works,
// without worrying about JWT tokens or 403 Forbidden errors interfering.
class AuthControllerTest {

    // The "Fake Browser" that lets us send requests (GET, POST) in code.
    @Autowired private MockMvc mockMvc;

    // The tool to convert Java Objects -> JSON String.
    @Autowired private ObjectMapper objectMapper;

    // We create "Fake" versions (Mocks) of all the dependencies the Controller needs.
    // The Controller doesn't know they are fake; it treats them like real services.
    @MockBean private AuthService authService;
    @MockBean private JwtUtil jwtUtil;
    @MockBean private JwtFilter jwtFilter;
    @MockBean private UserRepository userRepository;

    /**
     * Test Case 1: A normal user signs up.
     * We expect the system to say "User Registered as USER".
     */
    @Test
    void shouldRegisterNormalUser() throws Exception {
        // 1. Prepare the input data (The form filled out by the user).
        // Notice the 'null' at the end - No Admin Key provided.
        AuthRequestDto request = new AuthRequestDto("testUser", "password123", null);

        // 2. Script the Actor (Mock).
        // We tell the fake Service: "If anyone asks you to register a user, just say 'User Registered as USER'."
        when(authService.register(any(AuthRequestDto.class))).thenReturn("User Registered as USER");

        // 3. Perform the actual Request (The Action).
        mockMvc.perform(post("/api/auth/register") // Go to this URL
                        .contentType(MediaType.APPLICATION_JSON) // Send data as JSON
                        .content(objectMapper.writeValueAsString(request))) // Convert our Java object to JSON text

                // 4. Verify the Result (The Expectation).
                .andExpect(status().isCreated()) // Expect HTTP 201 (Created)
                .andExpect(jsonPath("$.success").value(true)) // Check JSON: { "success": true ... }
                .andExpect(jsonPath("$.message").value("User Registered as USER")); // Check the message
    }

    /**
     * Test Case 2: An ADMIN signs up.
     * We expect the system to acknowledge the VIP status.
     */
    @Test
    void shouldRegisterAdminUser() throws Exception {
        // 1. Prepare input data WITH the Secret Key.
        AuthRequestDto request = new AuthRequestDto("adminUser", "password123", "SweetShopMasterKey2025");

        // 2. Script the Actor (Mock).
        // We tell the fake Service: "If anyone asks, say this user is an ADMIN."
        when(authService.register(any(AuthRequestDto.class))).thenReturn("User Registered as ADMIN");

        // 3. Perform the Request.
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))

                // 4. Verify the Result.
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("User Registered as ADMIN"));
    }
}