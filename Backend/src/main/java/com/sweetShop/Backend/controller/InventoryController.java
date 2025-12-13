package com.sweetShop.Backend.controller;

import com.sweetShop.Backend.model.Sweet;
import com.sweetShop.Backend.service.InventoryService;
import com.sweetShop.Backend.util.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * The Inventory Controller.
 * This class handles the business transactions: Buying and Restocking.
 * It connects the web requests (clicks) to the inventory logic.
 */
@RestController // Marks this as a web request handler.
@RequestMapping("/api/sweets") // Both actions happen under the main "sweets" URL.
public class InventoryController {

    // The worker logic that actually calculates the math (stock - 1, or stock + 10).
    private final InventoryService inventoryService;

    // Constructor: Spring Boot automatically "injects" (provides) the service here.
    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    /**
     * Endpoint: POST /api/sweets/{id}/purchase
     * Example: POST /api/sweets/5/purchase
     * Purpose: A user buys a single sweet. The stock count decreases by 1.
     * * Security: @PreAuthorize check.
     * "hasAnyRole('USER','ADMIN')" means anyone with a valid login (User OR Admin) can buy sweets.
     */
    @PostMapping("/{id}/purchase")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<ApiResponse<Sweet>> purchase(@PathVariable Long id) {

        // 1. Call the service to perform the purchase logic (check stock > 0, decrease count).
        Sweet sweet = inventoryService.purchaseSweet(id);

        // 2. Return success message with the updated sweet details (showing the new lower quantity).
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Purchase successful", sweet)
        );
    }

    /**
     * Endpoint: POST /api/sweets/{id}/restock?quantity=10
     * Example: POST /api/sweets/5/restock?quantity=50
     * Purpose: Add more items to the inventory.
     * * Security: @PreAuthorize check.
     * "hasRole('ADMIN')" means ONLY Admins can do this. Normal users will get a 403 Forbidden error.
     */
    @PostMapping("/{id}/restock")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Sweet>> restock(
            @PathVariable Long id,        // The ID comes from the URL path (e.g., /5/restock)
            @RequestParam int quantity) { // The amount comes from the URL query (e.g., ?quantity=10)

        // 1. Call the service to add the new stock amount to the existing total.
        Sweet sweet = inventoryService.restockSweet(id, quantity);

        // 2. Return success message with the updated sweet details (showing the new higher quantity).
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Restock successful", sweet)
        );
    }
}