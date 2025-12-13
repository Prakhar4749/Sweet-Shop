package com.sweetShop.Backend.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The API Response Wrapper.
 * This class ensures that EVERY single response from our server looks exactly the same.
 * It creates a consistent "envelope" structure.
 * * The <T> stands for "Type". It is a generic placeholder.
 * It means this box can hold ANY type of data (a String, a Sweet object, a List of Users, etc.).
 */
@Data // Lombok: Generates Getters, Setters, etc.
@AllArgsConstructor // Lombok: Constructor for all fields.
@NoArgsConstructor // Lombok: Empty constructor.
public class ApiResponse<T> {

    // 1. Status Flag: Did the request work?
    // True = "Green Light" (Proceed).
    // False = "Red Light" (Something went wrong, check the message).
    private boolean success;

    // 2. Human-Readable Message.
    // E.g., "Login Successful", "Sweet added", or "Invalid Password".
    // This helps the developer (or user) understand what happened.
    private String message;

    // 3. The Actual Content (The "Payload").
    // This is the variable part. It changes based on what we are sending.
    // If <T> is a User, 'data' will be user details.
    // If <T> is a List<Sweet>, 'data' will be the menu.
    // If <T> is String, 'data' might be null or extra text.
    private T data;

    // Special Constructor:
    // Sometimes we just want to say "Success!" without sending any data back
    // (e.g., after deleting an item). This helper makes that easier.
    public ApiResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
        this.data = null;
    }
}