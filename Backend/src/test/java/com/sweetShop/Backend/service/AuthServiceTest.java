package com.sweetShop.Backend.service;

import com.sweetShop.Backend.config.JwtUtil;
import com.sweetShop.Backend.dto.AuthRequestDto;
import com.sweetShop.Backend.dto.AuthResponseDto;
import com.sweetShop.Backend.model.User;
import com.sweetShop.Backend.repository.UserRepository;
import com.sweetShop.Backend.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor; // Needed to capture the User object being saved
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthServiceImpl authService;

    // --- Register Tests ---

    @Test
    void shouldRegisterUserAsNormalUserByDefault() {
        // Arrange: No admin key provided
        AuthRequestDto request = new AuthRequestDto("newUser", "pass123", null);
        when(userRepository.findByUsername(request.getUsername())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedPass");

        // Act
        String result = authService.register(request);

        // Assert: Capture the user object saved to the DB to check its role
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();
        assertEquals(User.Role.USER, savedUser.getRole()); // Verify Role is USER
        assertTrue(result.contains("USER"));
    }

    @Test
    void shouldRegisterUserAsAdminWithCorrectKey() {
        // Arrange: Correct Admin Key provided
        // Make sure this matches the key in your AuthServiceImpl ("SweetShopMasterKey2025")
        AuthRequestDto request = new AuthRequestDto("adminUser", "pass123", "SweetShopMasterKey2025");

        when(userRepository.findByUsername(request.getUsername())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedPass");

        // Act
        String result = authService.register(request);

        // Assert
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();
        assertEquals(User.Role.ADMIN, savedUser.getRole()); // Verify Role is ADMIN
        assertTrue(result.contains("ADMIN"));
    }

    @Test
    void shouldRegisterUserAsNormalUserWithWrongKey() {
        // Arrange: Wrong Admin Key provided
        AuthRequestDto request = new AuthRequestDto("hacker", "pass123", "WrongKey");

        when(userRepository.findByUsername(request.getUsername())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedPass");

        // Act
        authService.register(request);

        // Assert
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();
        assertEquals(User.Role.USER, savedUser.getRole());
    }

    @Test
    void shouldThrowExceptionWhenRegisteringExistingUser() {
        AuthRequestDto request = new AuthRequestDto("existingUser", "pass123", null);
        when(userRepository.findByUsername(request.getUsername())).thenReturn(Optional.of(new User()));

        assertThrows(RuntimeException.class, () -> authService.register(request));
        verify(userRepository, never()).save(any(User.class));
    }



    @Test
    void shouldLoginSuccessfully() {
        // Arrange
        AuthRequestDto request = new AuthRequestDto("validUser", "pass123", null);
        User user = new User(1L, "validUser", "encodedPass", User.Role.USER);

        when(userRepository.findByUsername(request.getUsername())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(request.getPassword(), user.getPassword())).thenReturn(true);

        // UPDATED: Mock now expects (username, role)
        when(jwtUtil.generateToken("validUser", "USER")).thenReturn("mock.jwt.token");

        // Act
        AuthResponseDto response = authService.login(request);

        // Assert
        assertNotNull(response);
        assertEquals("mock.jwt.token", response.getToken());
    }
}