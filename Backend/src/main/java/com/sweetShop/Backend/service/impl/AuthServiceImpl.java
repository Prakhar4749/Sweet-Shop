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

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    // Define your secret key here (In a real app, put this in application.properties)
    private static final String ADMIN_SECRET = "SweetShopMasterKey2025";

    @Override
    public String register(AuthRequestDto dto) {
        if(userRepository.findByUsername(dto.getUsername()).isPresent()) {
            // CHANGE THIS LINE:
            throw new UserAlreadyExistsException("Username already taken");
        }

        User user = new User();
        user.setUsername(dto.getUsername());

        user.setPassword(passwordEncoder.encode(dto.getPassword()));

        // Check if adminKey is present AND matches our secret
        if (dto.getAdminKey() != null && dto.getAdminKey().equals(ADMIN_SECRET)) {
            user.setRole(User.Role.ADMIN);
        } else {
            user.setRole(User.Role.USER);
        }


        userRepository.save(user);
        return "User Registered as " + user.getRole();
    }

    @Override
    public AuthResponseDto login(AuthRequestDto dto) {
        User user = userRepository.findByUsername(dto.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid Credentials");
        }

        // UPDATED: Pass the role name (e.g., "ADMIN" or "USER")
        String token = jwtUtil.generateToken(dto.getUsername(), user.getRole().name());

        return new AuthResponseDto(token);
    }
}