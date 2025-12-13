package com.sweetShop.Backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sweetShop.Backend.config.JwtUtil;
import com.sweetShop.Backend.dto.SweetRequestDto;
import com.sweetShop.Backend.model.Sweet;
import com.sweetShop.Backend.service.SweetService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Sweet Controller Test.
 * This class specifically tests the "Menu Manager" (SweetController).
 * It verifies that our API correctly handles data and enforcing permissions.
 */
@WebMvcTest(controllers = SweetController.class) // Focus only on the SweetController.
@Import(TestMethodSecurityConfig.class) // Import our special Security Config for tests.
class SweetControllerTest {

    // The Fake Browser to send requests.
    @Autowired
    private MockMvc mockMvc;

    // The tool to convert Java Objects -> JSON text.
    @Autowired
    private ObjectMapper objectMapper;

    // The Fake Service (Actor).
    @MockBean
    private SweetService sweetService;

    // The Fake ID Machine (needed because Security is active).
    @MockBean
    private JwtUtil jwtUtil;

    /* ---------- TEST CASE 1: READING THE MENU (Allowed) ---------- */

    @Test
    @WithMockUser(roles = "USER") // Use a "Standard Customer" badge.
    void userShouldGetAllSweets() throws Exception {
        // 1. Script: "If asked for sweets, return a list containing one 'Laddu'."
        Sweet sweet = new Sweet(1L, "Laddu", "Indian", 10.0, 100);
        when(sweetService.getAllSweets()).thenReturn(List.of(sweet));

        // 2. Action: Go to GET /api/sweets
        mockMvc.perform(get("/api/sweets"))

                // 3. Verification:
                .andExpect(status().isOk()) // 200 OK.
                .andExpect(jsonPath("$.success").value(true)); // Check that the response is successful.
    }

    /* ---------- TEST CASE 2: ADDING ITEMS (Admin Only - Allowed) ---------- */

    @Test
    @WithMockUser(roles = "ADMIN") // Use a "Manager" badge.
    void adminShouldAddSweet() throws Exception {
        // 1. Setup: Prepare the form data for a new sweet.
        SweetRequestDto dto = new SweetRequestDto("Jalebi", "Indian", 20.0, 50);

        // 2. Script: "If asked to add a sweet, return the created object."
        when(sweetService.addSweet(any()))
                .thenReturn(new Sweet(1L, "Jalebi", "Indian", 20.0, 50));

        // 3. Action: Send POST request with the JSON data.
        mockMvc.perform(post("/api/sweets")
                        .with(csrf()) // Security token for tests.
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))) // Convert form to JSON.

                // 4. Verification:
                .andExpect(status().isCreated()); // 201 Created.
    }

    /* ---------- TEST CASE 3: ADDING ITEMS (User - Forbidden) ---------- */

    @Test
    @WithMockUser(roles = "USER") // Use a "Standard Customer" badge.
    void userShouldNotAddSweet() throws Exception {
        SweetRequestDto dto = new SweetRequestDto("Barfi", "Indian", 30.0, 40);

        // Action: Customer tries to sneak a new item onto the menu.
        mockMvc.perform(post("/api/sweets")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))

                // Verification: Stopped by security.
                // 403 Forbidden = "You are logged in, but you don't have the rank to do this."
                .andExpect(status().isForbidden());
    }

    /* ---------- TEST CASE 4: ADDING ITEMS (Stranger - Unauthorized) ---------- */

    @Test
    void unauthenticatedUserCannotAddSweet() throws Exception {
        // Note: No @WithMockUser here. This simulates a stranger with NO badge.
        SweetRequestDto dto = new SweetRequestDto("Peda", "Indian", 25.0, 30);

        // Action: Stranger tries to add an item.
        mockMvc.perform(post("/api/sweets")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))

                // Verification: Stopped at the door.
                // 401 Unauthorized = "We don't know who you are. Login first."
                .andExpect(status().isUnauthorized());
    }
}