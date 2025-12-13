package com.sweetShop.Backend.controller;

import com.sweetShop.Backend.config.JwtUtil;
import com.sweetShop.Backend.model.Sweet;
import com.sweetShop.Backend.service.InventoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Inventory Controller Test.
 * This class tests the "Transaction Counter" in isolation.
 * We are specifically checking if the Permissions (Security) work correctly here.
 */
@WebMvcTest(controllers = InventoryController.class) // Focus ONLY on the Inventory Controller.
@Import(TestMethodSecurityConfig.class) // Import our special "Test Security Rules" helper.
class InventoryControllerTest {

    // The Fake Browser.
    @Autowired
    private MockMvc mockMvc;

    // The Fake Inventory Worker (Service).
    @MockBean
    private InventoryService inventoryService;

    // The Fake ID Card Machine (JWT Util).
    @MockBean
    private JwtUtil jwtUtil;


    /* ---------- TEST CASE 1: HAPPY PATH (Buying) ---------- */

    @Test
    @WithMockUser(roles = "USER") // Pretend the person running this test has a "USER" badge.
    void userShouldPurchaseSweet() throws Exception {
        // 1. Setup: Prepare a sweet that has 9 items left.
        Sweet sweet = new Sweet(1L, "Barfi", "Milk", 10.0, 9);

        // 2. Script: Tell the worker "If someone buys item #1, return this sweet object."
        when(inventoryService.purchaseSweet(1L)).thenReturn(sweet);

        // 3. Action: Send a POST request to buy sweet #1.
        mockMvc.perform(post("/api/sweets/1/purchase")
                        .with(csrf())) // CSRF is a security token needed for POST requests in tests.

                // 4. Verification:
                .andExpect(status().isOk()) // The counter should say "OK".
                .andExpect(jsonPath("$.success").value(true)) // Success flag is true.
                .andExpect(jsonPath("$.message").value("Purchase successful")) // Message is correct.
                .andExpect(jsonPath("$.data.quantity").value(9)); // The data shows the new quantity.
    }


    /* ---------- TEST CASE 2: HAPPY PATH (Restocking) ---------- */

    @Test
    @WithMockUser(roles = "ADMIN") // Pretend the person running this test has an "ADMIN" badge.
    void adminShouldRestockSweet() throws Exception {
        // 1. Setup: Prepare a sweet that now has 20 items (after adding 10).
        Sweet sweet = new Sweet(1L, "Barfi", "Milk", 10.0, 20);

        // 2. Script: Tell the worker "If someone restocks item #1 with 10 units, return this result."
        when(inventoryService.restockSweet(1L, 10)).thenReturn(sweet);

        // 3. Action: Send POST request. Notice we use .param() because it's a ?quantity=10 URL parameter.
        mockMvc.perform(post("/api/sweets/1/restock")
                        .with(csrf())
                        .param("quantity", "10"))

                // 4. Verification:
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Restock successful"))
                .andExpect(jsonPath("$.data.quantity").value(20));
    }


    /* ---------- TEST CASE 3: SECURITY CHECK (Forbidden) ---------- */

    @Test
    @WithMockUser(roles = "USER") // This person only has a "USER" badge.
    void userShouldNotRestockSweet() throws Exception {
        // Action: A normal user tries to run the "Restock" command.
        mockMvc.perform(post("/api/sweets/1/restock")
                        .with(csrf())
                        .param("quantity", "10"))

                // Verification: They should be stopped immediately.
                // 403 Forbidden = "I know who you are, but you aren't allowed to do this."
                .andExpect(status().isForbidden());
    }


    /* ---------- TEST CASE 4: SECURITY CHECK (Unauthorized) ---------- */

    @Test
    void unauthenticatedUserCannotPurchase() throws Exception {
        // Action: Someone WITHOUT any badge (@WithMockUser is missing) tries to buy.
        mockMvc.perform(post("/api/sweets/1/purchase")
                        .with(csrf()))

                // Verification: They should be stopped at the door.
                // 401 Unauthorized = "I don't know who you are. Login first."
                .andExpect(status().isUnauthorized());
    }

    @Test
    void unauthenticatedUserCannotRestock() throws Exception {
        // Action: Someone WITHOUT any badge tries to restock.
        mockMvc.perform(post("/api/sweets/1/restock")
                        .with(csrf())
                        .param("quantity", "10"))

                // Verification: 401 Unauthorized.
                .andExpect(status().isUnauthorized());
    }
}