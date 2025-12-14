package com.sweetShop.Backend.config;

import com.sweetShop.Backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * The Master Security Configuration.
 * This class defines the high-level rules for how our application protects itself.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)// ✅ Required so we can use @PreAuthorize("hasRole('ADMIN')") on specific methods
@RequiredArgsConstructor
public class SecurityConfig {

    // Our custom components we built earlier
    private final JwtFilter jwtFilter; // The "Checkpoint"
    private final JwtAuthenticationEntryPoint authenticationEntryPoint; // The "Front Door Security" (401)
    private final JwtAccessDeniedHandler accessDeniedHandler; // The "VIP Bouncer" (403)

    /**
     * The Security Filter Chain defines the exact order of operations for every request.
     * Think of this as the "Standard Operating Procedure" for the security guards.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                // 1. Disable CSRF (Cross-Site Request Forgery).
                // This is a protection needed for session-based apps (like banking websites using cookies).
                // Since we use JWT Tokens (stateless), we don't need this complex check.
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                // 2. Set Session Policy to STATELESS.
                // This means the server has "amnesia." It will NOT remember a user is logged in
                // between clicks. The user must show their Token (ID Card) for EVERY single request.
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // 3. Plug in our Custom Error Handlers.
                // If a security error happens, don't use the default Spring behavior.
                // Use our nice JSON responses instead.
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(authenticationEntryPoint) // Handle "Who are you?" errors
                        .accessDeniedHandler(accessDeniedHandler)         // Handle "You can't go there" errors
                )

                // 4. Define the Access Rules (The "Guest List").
                .authorizeHttpRequests(auth -> auth
                        // ALLOW everyone to access Login and Register pages. No token needed.
                        .requestMatchers("/api/auth/**").permitAll()

                        // LOCK everything else. You must be logged in to see anything else.
                        .anyRequest().authenticated()
                )

                // 5. Insert our Custom JWT Filter.
                // Critical: We put our filter *before* the standard UsernamePasswordAuthenticationFilter.
                // We want to check the Token ID card first, before asking for a username/password login.
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Define the Password Encoder tool.
     * When a user logs in, we use this to scramble their input and compare it
     * to the scrambled password stored in our database.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Expose the Authentication Manager.
     * This is the internal engine Spring uses to verify credentials during the login process.
     */
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}