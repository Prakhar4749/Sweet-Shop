package com.sweetShop.Backend.service.impl;

import com.sweetShop.Backend.model.User;
import com.sweetShop.Backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Custom User Details Service.
 * This class acts as a bridge between our Database and Spring Security.
 * It teaches Spring Security how to look up users in OUR specific database table.
 */
@Service // Marks this as a service so Spring Security can find and use it automatically.
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    // Access to the database to find user records.
    private final UserRepository userRepository;

    /**
     * This method is called automatically by Spring whenever someone tries to log in.
     * Spring says: "I have a username 'john_doe'. Go find the details for this person."
     * * @param username The username typed in the login box.
     * @return A 'UserDetails' object (Standard format) containing the password and roles.
     * @throws UsernameNotFoundException If the user doesn't exist.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        // 1. Go to the database and look for the user.
        // If not found, throw an error immediately to stop the login process.
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // 2. Convert our Database 'User' into a Spring Security 'UserDetails' object.
        // We are taking data from our format and putting it into Spring's format.
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername()) // Copy the username
                .password(user.getPassword()) // Copy the encrypted password
                .roles(user.getRole().name()) // Copy the role (e.g., "ADMIN") so Spring knows permissions.
                .build();
    }
}