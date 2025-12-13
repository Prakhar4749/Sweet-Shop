package com.sweetShop.Backend.controller;

import com.sweetShop.Backend.dto.SweetRequestDto;
import com.sweetShop.Backend.model.Sweet;
import com.sweetShop.Backend.service.SweetService;
import com.sweetShop.Backend.util.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * The Sweet Controller.
 * This acts as the "Menu Manager" for the shop.
 * It handles creating, listing, finding, updating, and deleting sweets.
 */
@RestController
@RequestMapping("/api/sweets") // All these tools are found under the "/api/sweets" address.
@RequiredArgsConstructor
public class SweetController {

    // The service that does the actual work (database lookups, saving data).
    private final SweetService sweetService;

    /**
     * Endpoint: POST /api/sweets
     * Purpose: Add a BRAND NEW sweet to the shop.
     * Security: ADMIN ONLY. We don't want customers adding their own items.
     * @param dto The details of the new sweet (Name, Price, Category).
     * @return The created sweet with a success message.
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Sweet>> addSweet(@RequestBody @Valid SweetRequestDto dto) {

        // 1. Send the data to the service to be saved.
        // @Valid ensures the data is good (e.g., Price isn't negative, Name isn't empty).
        Sweet sweet = sweetService.addSweet(dto);

        // 2. Return a "201 Created" status to confirm it was successfully made.
        return new ResponseEntity<>(
                new ApiResponse<>(true, "Sweet added successfully", sweet),
                HttpStatus.CREATED
        );
    }

    /**
     * Endpoint: GET /api/sweets
     * Purpose: Show the full menu.
     * Security: Open to everyone (Authenticated users).
     * @return A list of every sweet in the database.
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<Sweet>>> getAllSweets() {
        // 1. Fetch the list from the database.
        List<Sweet> sweets = sweetService.getAllSweets();

        // 2. Return the list wrapped in our standard response format.
        return ResponseEntity.ok(new ApiResponse<>(true, "Fetched all sweets", sweets));
    }

    /**
     * Endpoint: GET /api/sweets/{id}
     * Purpose: Look up ONE specific sweet by its ID.
     * @param id The ID number of the sweet (e.g., 5).
     * @return The details of that single sweet.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Sweet>> getSweetById(@PathVariable Long id) {
        Sweet sweet = sweetService.getSweetById(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Sweet found", sweet));
    }

    /**
     * Endpoint: GET /api/sweets/search
     * Purpose: Find sweets based on specific criteria (Name, Price Range).
     * Example: /api/sweets/search?query=Milk&maxPrice=50
     * @param query Text to match against Name or Category (Optional).
     * @param minPrice Minimum price filter (Optional).
     * @param maxPrice Maximum price filter (Optional).
     * @return A list of sweets that match the filters.
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<Sweet>>> searchSweets(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice) {

        List<Sweet> results = sweetService.searchSweets(query, minPrice, maxPrice);
        return ResponseEntity.ok(new ApiResponse<>(true, "Search results", results));
    }

    /**
     * Endpoint: PUT /api/sweets/{id}
     * Purpose: Edit an existing sweet (e.g., Change price, fix spelling).
     * Security: ADMIN ONLY.
     * @param id The ID of the sweet to edit.
     * @param dto The new details to overwrite the old ones.
     * @return The updated sweet object.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Sweet>> updateSweet(@PathVariable Long id, @RequestBody @Valid SweetRequestDto dto) {
        Sweet updatedSweet = sweetService.updateSweet(id, dto);
        return ResponseEntity.ok(new ApiResponse<>(true, "Sweet updated successfully", updatedSweet));
    }

    /**
     * Endpoint: DELETE /api/sweets/{id}
     * Purpose: Permanently remove a sweet from the shop.
     * Security: ADMIN ONLY.
     * @param id The ID of the sweet to delete.
     * @return Success message (Data is null because the item is gone).
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteSweet(@PathVariable Long id) {
        sweetService.deleteSweet(id);
        // Note: data is null here because the object no longer exists to be returned.
        return ResponseEntity.ok(new ApiResponse<>(true, "Sweet deleted successfully", null));
    }
}