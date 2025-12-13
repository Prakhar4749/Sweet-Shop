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
@WebMvcTest(controllers = SweetController.class)
@Import(TestMethodSecurityConfig.class)
class SweetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SweetService sweetService;

    @MockBean
    private JwtUtil jwtUtil;

    /* ---------- USER GET ---------- */

    @Test
    @WithMockUser(roles = "USER")
    void userShouldGetAllSweets() throws Exception {
        Sweet sweet = new Sweet(1L, "Laddu", "Indian", 10.0, 100);
        when(sweetService.getAllSweets()).thenReturn(List.of(sweet));

        mockMvc.perform(get("/api/sweets"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    /* ---------- ADMIN ---------- */

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminShouldAddSweet() throws Exception {
        SweetRequestDto dto = new SweetRequestDto("Jalebi", "Indian", 20.0, 50);

        when(sweetService.addSweet(any()))
                .thenReturn(new Sweet(1L, "Jalebi", "Indian", 20.0, 50));

        mockMvc.perform(post("/api/sweets")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());
    }

    /* ---------- USER BLOCKED ---------- */

    @Test
    @WithMockUser(roles = "USER")
    void userShouldNotAddSweet() throws Exception {
        SweetRequestDto dto = new SweetRequestDto("Barfi", "Indian", 30.0, 40);

        mockMvc.perform(post("/api/sweets")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden());
    }

    /* ---------- NO AUTH ---------- */

    @Test
    void unauthenticatedUserCannotAddSweet() throws Exception {
        SweetRequestDto dto = new SweetRequestDto("Peda", "Indian", 25.0, 30);

        mockMvc.perform(post("/api/sweets")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnauthorized());
    }
}
