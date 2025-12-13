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

@WebMvcTest(controllers = InventoryController.class)
@Import(TestMethodSecurityConfig.class)
class InventoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private InventoryService inventoryService;

    @MockBean
    private JwtUtil jwtUtil;



    @Test
    @WithMockUser(roles = "USER")
    void userShouldPurchaseSweet() throws Exception {
        Sweet sweet = new Sweet(1L, "Barfi", "Milk", 10.0, 9);
        when(inventoryService.purchaseSweet(1L)).thenReturn(sweet);

        mockMvc.perform(post("/api/sweets/1/purchase")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Purchase successful"))
                .andExpect(jsonPath("$.data.quantity").value(9));
    }



    @Test
    @WithMockUser(roles = "ADMIN")
    void adminShouldRestockSweet() throws Exception {
        Sweet sweet = new Sweet(1L, "Barfi", "Milk", 10.0, 20);
        when(inventoryService.restockSweet(1L, 10)).thenReturn(sweet);

        // Sending quantity as a Request Parameter to match your Controller definition
        mockMvc.perform(post("/api/sweets/1/restock")
                        .with(csrf())
                        .param("quantity", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Restock successful"))
                .andExpect(jsonPath("$.data.quantity").value(20));
    }



    @Test
    @WithMockUser(roles = "USER")
    void userShouldNotRestockSweet() throws Exception {
        mockMvc.perform(post("/api/sweets/1/restock")
                        .with(csrf())
                        .param("quantity", "10"))
                .andExpect(status().isForbidden());
    }



    @Test
    void unauthenticatedUserCannotPurchase() throws Exception {
        mockMvc.perform(post("/api/sweets/1/purchase")
                        .with(csrf()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void unauthenticatedUserCannotRestock() throws Exception {
        mockMvc.perform(post("/api/sweets/1/restock")
                        .with(csrf())
                        .param("quantity", "10"))
                .andExpect(status().isUnauthorized());
    }
}