package com.sweetShop.Backend.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sweetShop.Backend.util.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * This component acts as the first line of defense for the application.
 * It triggers whenever an unauthenticated user (someone not logged in)
 * tries to access a page that requires a login (like /api/sweets).
 */
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    // A utility tool used to convert Java objects into JSON format
    // so the browser or frontend app can understand the response.
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * This method "commences" (starts) the error handling process when someone fails authentication.
     * * @param request The incoming request (e.g., "Get me the list of sweets").
     * @param response The blank response we will fill with our error message.
     * @param authException The specific error details explaining why authentication failed.
     */
    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException {

        // 1. Set the status code to 401 (Unauthorized).
        // This tells the browser/frontend: "You are not logged in, or your session expired."
        // (Note: This is different from 403 Forbidden, which means "You are logged in, but not an Admin").
        response.setStatus(HttpStatus.UNAUTHORIZED.value());

        // 2. Tell the receiver that the error message will be in JSON format.
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        // 3. Construct a clean, standard error message using our unified ApiResponse format.
        // We explicitly set 'success' to false so the frontend knows something went wrong.
        ApiResponse<String> apiResponse =
                new ApiResponse<>(false, "Authentication required or token invalid", null);

        // 4. Convert the Java 'apiResponse' object into a JSON string and write it
        // to the response stream so it gets sent back to the user.
        response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
    }
}