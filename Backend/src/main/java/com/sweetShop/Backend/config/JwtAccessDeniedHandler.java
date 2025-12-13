package com.sweetShop.Backend.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sweetShop.Backend.util.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * This component acts as our custom security guard for permission issues.
 * It triggers ONLY when a valid user tries to do something they are not allowed to do
 * (e.g., A normal user trying to delete a sweet).
 */
@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

    // A utility tool that converts our Java objects into JSON text
    // (the language the frontend/browser understands).
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * This method is automatically called by the system whenever an "AccessDeniedException" occurs.
     * * @param request The incoming request (what the user tried to do).
     * @param response The outgoing response (what we send back to them).
     * @param accessDeniedException The actual error details.
     */
    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException
    ) throws IOException {

        // 1. Set the status code to 403 (Forbidden).
        // This is the universal web signal for "I know who you are, but you can't come in."
        response.setStatus(HttpStatus.FORBIDDEN.value());

        // 2. Tell the browser/frontend that we are sending the answer back in JSON format.
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        // 3. Create a neat, standardized error message using our ApiResponse wrapper.
        // We set success to 'false' and provide a clear English message.
        ApiResponse<String> apiResponse =
                new ApiResponse<>(false, "You do not have permission to access this resource", null);

        // 4. Actually write this message to the response stream so the user sees it.
        // We use objectMapper to turn our Java 'apiResponse' object into a JSON string like:
        // { "success": false, "message": "You do not have permission...", "data": null }
        response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
    }
}