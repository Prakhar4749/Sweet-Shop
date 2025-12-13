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

/**
 * Auth Service Test.
 * This class tests the "Brain" of the registration/login process.
 * It does NOT use the real database. It uses "Mocks" (simulations).
 */
@ExtendWith(MockitoExtension.class) // Tells JUnit to set up the "Mocking" laboratory.
class AuthServiceTest {

    // A Fake Database. We can program it to say "User found" or "User not found".
    @Mock
    private UserRepository userRepository;

    // A Fake Password Scrambler. We don't need real encryption here, just a placeholder.
    @Mock
    private PasswordEncoder passwordEncoder;

    // A Fake Token Machine.
    @Mock
    private JwtUtil jwtUtil;

    // The Real Service we are testing.
    // Mockito automatically injects the fake tools (mocks) above into this service.
    @InjectMocks
    private AuthServiceImpl authService;

    // --- Register Tests ---

    /**
     * Test Case 1: Normal Registration.
     * Logic Check: If no admin key is provided, do they become a USER?
     */
    @Test
    void shouldRegisterUserAsNormalUserByDefault() {
        // 1. Arrange (Setup the simulation):
        // Create a form with NO secret key (null).
        AuthRequestDto request = new AuthRequestDto("newUser", "pass123", null);

        // Script the fake database: "If asked for 'newUser', say they don't exist (Optional.empty)."
        when(userRepository.findByUsername(request.getUsername())).thenReturn(Optional.empty());
        // Script the fake encoder: "If asked to encode, just return 'encodedPass'."
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedPass");

        // 2. Act (Run the code):
        String result = authService.register(request);

        // 3. Assert (Verify the results):
        // We use an "ArgumentCaptor" to spy on exactly what data the service tried to save.
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

        // verify() checks: Did the service actually call 'save'?
        // userCaptor.capture() grabs the User object so we can inspect it.
        verify(userRepository).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();
        // Check: Is the role USER?
        assertEquals(User.Role.USER, savedUser.getRole());
        // Check: Does the success message contain "USER"?
        assertTrue(result.contains("USER"));
    }

    /**
     * Test Case 2: Admin Registration.
     * Logic Check: If the CORRECT secret key is provided, do they become an ADMIN?
     */
    @Test
    void shouldRegisterUserAsAdminWithCorrectKey() {
        // Arrange: Provide the correct secret key ("SweetShopMasterKey2025").
        AuthRequestDto request = new AuthRequestDto("adminUser", "pass123", "SweetShopMasterKey2025");

        when(userRepository.findByUsername(request.getUsername())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedPass");

        // Act
        String result = authService.register(request);

        // Assert
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();
        // Check: Is the role ADMIN?
        assertEquals(User.Role.ADMIN, savedUser.getRole());
        assertTrue(result.contains("ADMIN"));
    }

    /**
     * Test Case 3: Failed Admin Attempt.
     * Logic Check: If a WRONG key is provided, do they get downgraded to USER?
     */
    @Test
    void shouldRegisterUserAsNormalUserWithWrongKey() {
        // Arrange: Provide a junk key ("WrongKey").
        AuthRequestDto request = new AuthRequestDto("hacker", "pass123", "WrongKey");

        when(userRepository.findByUsername(request.getUsername())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedPass");

        // Act
        authService.register(request);

        // Assert
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();
        // Check: Role must default to USER.
        assertEquals(User.Role.USER, savedUser.getRole());
    }

    /**
     * Test Case 4: Duplicate User.
     * Logic Check: If the username exists, do we stop and throw an error?
     */
    @Test
    void shouldThrowExceptionWhenRegisteringExistingUser() {
        AuthRequestDto request = new AuthRequestDto("existingUser", "pass123", null);

        // Script: "If asked, say this user ALREADY exists."
        when(userRepository.findByUsername(request.getUsername())).thenReturn(Optional.of(new User()));

        // Act & Assert: Expect a RuntimeException.
        assertThrows(RuntimeException.class, () -> authService.register(request));

        // Verify: Ensure the 'save' method was NEVER called.
        verify(userRepository, never()).save(any(User.class));
    }


    /**
     * Test Case 5: Successful Login.
     * Logic Check: If user exists and password matches, do we get a token?
     */
    @Test
    void shouldLoginSuccessfully() {
        // Arrange
        AuthRequestDto request = new AuthRequestDto("validUser", "pass123", null);
        // Create the user that "exists" in the DB.
        User user = new User(1L, "validUser", "encodedPass", User.Role.USER);

        // Script: User found.
        when(userRepository.findByUsername(request.getUsername())).thenReturn(Optional.of(user));
        // Script: Password matches.
        when(passwordEncoder.matches(request.getPassword(), user.getPassword())).thenReturn(true);

        // Script: Token generator works.
        // Important: We match the arguments ("validUser", "USER").
        when(jwtUtil.generateToken("validUser", "USER")).thenReturn("mock.jwt.token");

        // Act
        AuthResponseDto response = authService.login(request);

        // Assert
        assertNotNull(response); // We got a response.
        assertEquals("mock.jwt.token", response.getToken()); // It contains our token.
    }
}