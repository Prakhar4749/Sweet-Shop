package com.sweetShop.Backend.controller;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

/**
 * Test Security Configuration.
 * This is a tiny helper class created SPECIFICALLY for our Controller Tests.
 * * Problem: By default, @WebMvcTest focuses only on the URL endpoints and ignores
 * complex security rules like @PreAuthorize("hasRole('ADMIN')").
 * * Solution: This class forces the test environment to turn those rules ON.
 */
@Configuration // Tells Spring: "This is a setup file."
@EnableMethodSecurity // Tells Spring: "Please activate the @PreAuthorize annotations."
class TestMethodSecurityConfig {
    // This class is empty because its only job is to carry the annotations above.
    // It acts like a flag or a switch.
}