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

@RestController
@RequestMapping("/api/sweets")
@RequiredArgsConstructor
public class SweetController {

    private final SweetService sweetService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Sweet>> addSweet(@RequestBody @Valid SweetRequestDto dto) {
        Sweet sweet = sweetService.addSweet(dto);
        return new ResponseEntity<>(
                new ApiResponse<>(true, "Sweet added successfully", sweet),
                HttpStatus.CREATED
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Sweet>>> getAllSweets() {
        List<Sweet> sweets = sweetService.getAllSweets();
        return ResponseEntity.ok(new ApiResponse<>(true, "Fetched all sweets", sweets));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Sweet>> getSweetById(@PathVariable Long id) {
        Sweet sweet = sweetService.getSweetById(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Sweet found", sweet));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<Sweet>>> searchSweets(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice) {
        List<Sweet> results = sweetService.searchSweets(query, minPrice, maxPrice);
        return ResponseEntity.ok(new ApiResponse<>(true, "Search results", results));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Sweet>> updateSweet(@PathVariable Long id, @RequestBody @Valid SweetRequestDto dto) {
        Sweet updatedSweet = sweetService.updateSweet(id, dto);
        return ResponseEntity.ok(new ApiResponse<>(true, "Sweet updated successfully", updatedSweet));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteSweet(@PathVariable Long id) {
        sweetService.deleteSweet(id);
        // Note: data is null here
        return ResponseEntity.ok(new ApiResponse<>(true, "Sweet deleted successfully", null));
    }
}