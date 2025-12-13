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

@WebMvcTest(controllers = AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockBean private AuthService authService;
    @MockBean private JwtUtil jwtUtil;
    @MockBean private JwtFilter jwtFilter;
    @MockBean private UserRepository userRepository;

    @Test
    void shouldRegisterNormalUser() throws Exception {
        // Request without adminKey
        AuthRequestDto request = new AuthRequestDto("testUser", "password123", null);

        // Service returns standard user message
        when(authService.register(any(AuthRequestDto.class))).thenReturn("User Registered as USER");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("User Registered as USER"));
    }

    @Test
    void shouldRegisterAdminUser() throws Exception {
        // Request WITH adminKey
        AuthRequestDto request = new AuthRequestDto("adminUser", "password123", "SweetShopMasterKey2025");

        // Service returns ADMIN message
        when(authService.register(any(AuthRequestDto.class))).thenReturn("User Registered as ADMIN");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("User Registered as ADMIN"));
    }
}