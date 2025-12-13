package com.sweetShop.Backend.controller;

import com.sweetShop.Backend.model.Sweet;
import com.sweetShop.Backend.service.InventoryService;
import com.sweetShop.Backend.util.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/sweets")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @PostMapping("/{id}/purchase")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<ApiResponse<Sweet>> purchase(@PathVariable Long id) {
        Sweet sweet = inventoryService.purchaseSweet(id);
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Purchase successful", sweet)
        );
    }

    @PostMapping("/{id}/restock")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Sweet>> restock(
            @PathVariable Long id,
            @RequestParam int quantity) {

        Sweet sweet = inventoryService.restockSweet(id, quantity);
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Restock successful", sweet)
        );
    }
}
