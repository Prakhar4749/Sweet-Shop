package com.sweetShop.Backend.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * This component intercepts every single request coming into the application.
 * Its job is to look for a "Bearer Token" (the digital ID card) in the request header.
 */
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    // Helper tool to decode and read the JWT token
    private final JwtUtil jwtUtil;

    // Service to load full user details (like password/roles) from the database
    private final UserDetailsService userDetailsService;



    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return request.getServletPath().startsWith("/api/auth");
    }


    /**
     * This is the core method that runs for every request.
     * * @param request  The incoming request (e.g., POST /api/sweets)
     * @param response The response we are building
     * @param filterChain The chain of other filters (the rest of the security guards)
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // 1. Look for the "Authorization" header in the incoming request.
        // This is where the frontend sends the token like: "Bearer eyJhbGci..."
        String authHeader = request.getHeader("Authorization");
        String token = null;
        String username = null;

        // 2. Check if the header exists and starts with "Bearer ".
        // "Bearer" is just a standard prefix, like saying "ID Card: <Number>"
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            // Cut off the first 7 characters ("Bearer ") to get the actual token code.
            token = authHeader.substring(7);

            // Use our JwtUtil tool to open the token and read the username written inside it.
            username = jwtUtil.extractUsername(token);
        }

        // 3. If we found a username AND the user is not already logged in...
        // (SecurityContextHolder is the system's "memory" of who is currently logged in)
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // 4. Fetch the full user details from our database (check if they actually exist).
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            // 5. Double-check: Is the token valid? (Not expired, signature matches, etc.)
            if (jwtUtil.validateToken(token, userDetails.getUsername())) {

                // 6. Create an "Authentication Token" object.
                // This acts like a stamped ticket saying "This user is verified."
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities()
                );

                // Add extra details about the request (like IP address) to the token.
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // 7. FINAL STEP: Put this stamped ticket into the SecurityContext.
                // Now Spring Security knows this user is officially logged in for this request.
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // 8. Open the gate! Pass the request to the next filter or the Controller.
        filterChain.doFilter(request, response);
    }
}