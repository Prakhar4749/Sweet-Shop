package com.sweetShop.Backend.service.impl;

import com.sweetShop.Backend.dto.AuthRequestDto;
import com.sweetShop.Backend.dto.AuthResponseDto;
import com.sweetShop.Backend.exception.UserAlreadyExistsException;
import com.sweetShop.Backend.model.User;
import com.sweetShop.Backend.repository.UserRepository;
import com.sweetShop.Backend.service.AuthService;
import com.sweetShop.Backend.config.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * The Authentication Service Implementation.
 * This handles the actual business logic for Signing Up and Logging In.
 * It sits between the Controller (Web layer) and the Repository (Database layer).
 */
@Service // Marks this as a Service so Spring can use it.
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    // Access to the database to save/find users.
    private final UserRepository userRepository;

    // The tool that scrambles passwords (e.g., turns "1234" into "$2a$10$xyz...").
    private final PasswordEncoder passwordEncoder;

    // The machine that prints the Digital ID Cards (Tokens).
    private final JwtUtil jwtUtil;

    // A hardcoded secret key.
    // If a user knows this magic word, they become an Admin instantly.
    // (In a real app, put this in application.properties).
    private static final String ADMIN_SECRET = "SweetShopMasterKey2025";

    /**
     * Logic for registering a new user.
     * @param dto The form data (username, password, adminKey).
     * @return A success message describing the role assigned.
     */
    @Override
    public String register(AuthRequestDto dto) {
        // 1. Check if the username is already taken.
        if(userRepository.findByUsername(dto.getUsername()).isPresent()) {
            // If yes, stop immediately and throw a specific error.
            // This prevents duplicate users in the database.
            throw new UserAlreadyExistsException("Username already taken");
        }

        // 2. Create a blank User object to fill with data.
        User user = new User();
        user.setUsername(dto.getUsername());

        // 3. IMPORTANT: Scramble the password before saving!
        // We never store plain passwords. If we get hacked, the hackers only see gibberish.
        user.setPassword(passwordEncoder.encode(dto.getPassword()));

        // 4. Determine the Role (The "Backdoor" Logic).
        // Check: Did the user provide a key? AND Does it match our secret?
        if (dto.getAdminKey() != null && dto.getAdminKey().equals(ADMIN_SECRET)) {
            // Yes! Grant them ADMIN powers.
            user.setRole(User.Role.ADMIN);
        } else {
            // No key or wrong key? They are just a normal USER.
            user.setRole(User.Role.USER);
        }

        // 5. Save the complete user profile to the database.
        userRepository.save(user);

        return "User Registered as " + user.getRole();
    }

    /**
     * Logic for logging in a user.
     * @param dto The login credentials.
     * @return A package containing the JWT Token.
     */
    @Override
    public AuthResponseDto login(AuthRequestDto dto) {
        // 1. Find the user in the database.
        // If they don't exist, throw an error immediately.
        User user = userRepository.findByUsername(dto.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 2. Verify the Password.
        // We use .matches() because we have to compare the plain text "password123"
        // against the scrambled hash "$2a$10$..." stored in the DB.
        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid Credentials");
        }

        // 3. Generate the Token (Print the ID Card).
        // We include the username and their Role (e.g., "ADMIN") inside the token.
        String token = jwtUtil.generateToken(dto.getUsername(), user.getRole().name());

        // 4. Return the token wrapped in our response object.
        return new AuthResponseDto(token);
    }
}