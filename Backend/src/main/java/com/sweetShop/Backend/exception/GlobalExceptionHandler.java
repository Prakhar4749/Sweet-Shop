package com.sweetShop.Backend.exception;

import com.sweetShop.Backend.util.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * The Global Exception Handler.
 * This acts as a centralized "Safety Net" for the entire application.
 * If ANY Controller throws an error, it is caught here so we can send a clean JSON response
 * instead of a messy HTML error page or stack trace.
 */
@RestControllerAdvice // Tells Spring: "Watch ALL Controllers. If they throw an error, come to me."
public class GlobalExceptionHandler {

    // 1. Scenario: Someone tries to register with a username that is already taken.
    // Result: 409 Conflict (Like trying to sit in an occupied chair).
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ApiResponse<String>> handleUserAlreadyExists(UserAlreadyExistsException ex) {
        return new ResponseEntity<>(
                // We create a standard response saying "Success: False" and pass the error message.
                new ApiResponse<>(false, ex.getMessage(), null),
                HttpStatus.CONFLICT
        );
    }

    // 2. Scenario: Someone looks for a Sweet (e.g., ID 999) that doesn't exist.
    // Result: 404 Not Found.
    @ExceptionHandler(SweetNotFoundException.class)
    public ResponseEntity<ApiResponse<String>> handleSweetNotFound(SweetNotFoundException ex) {
        return new ResponseEntity<>(
                new ApiResponse<>(false, ex.getMessage(), null),
                HttpStatus.NOT_FOUND
        );
    }

    // 3. Scenario: General logic errors (e.g., trying to buy a sweet with 0 stock).
    // Result: 400 Bad Request (The server understands the request but refuses to do it).
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<String>> handleRuntimeException(RuntimeException ex) {
        return new ResponseEntity<>(
                new ApiResponse<>(false, ex.getMessage(), null),
                HttpStatus.BAD_REQUEST
        );
    }

    // 4. Scenario: The "Catch-All" for unknown disasters (e.g., Database crashes, NullPointer).
    // Result: 500 Internal Server Error (It's not the user's fault; the server broke).
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<String>> handleGeneralException(Exception ex) {
        return new ResponseEntity<>(
                // We send a generic message to avoid leaking sensitive system details to the user.
                new ApiResponse<>(false, "An unexpected error occurred", null),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    // 5. Scenario: A specific business rule for permissions was broken.
    // Result: 403 Forbidden.
    @ExceptionHandler(UnauthorizedOperationException.class)
    public ResponseEntity<ApiResponse<String>> handleUnauthorizedOperation(UnauthorizedOperationException ex) {
        return new ResponseEntity<>(
                new ApiResponse<>(false, ex.getMessage(), null),
                HttpStatus.FORBIDDEN
        );
    }

    // 6. Scenario: Spring Security blocks a user (e.g., User trying to access Admin page).
    // Result: 403 Forbidden.
    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<ApiResponse<String>> handleAuthorizationDenied(AuthorizationDeniedException ex) {
        return new ResponseEntity<>(
                new ApiResponse<>(false, "Access Denied", null),
                HttpStatus.FORBIDDEN
        );
    }

}