package com.sweetShop.Backend.service;

import com.sweetShop.Backend.dto.SweetRequestDto;
import com.sweetShop.Backend.exception.SweetNotFoundException;
import com.sweetShop.Backend.model.Sweet;
import com.sweetShop.Backend.repository.SweetRepository;
import com.sweetShop.Backend.service.impl.SweetServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Sweet Service Test.
 * This class tests the logic for managing the menu (Adding, Updating, Searching).
 * It ensures the Manager behaves correctly when data is missing or when searching.
 */
@ExtendWith(MockitoExtension.class) // Turn on the "Mocking" laboratory.
class SweetServiceTest {

    // The Fake Database.
    @Mock private SweetRepository sweetRepository;

    // The Real Service we are testing.
    @InjectMocks private SweetServiceImpl sweetService;

    /**
     * Test Case 1: Updating a sweet that doesn't exist.
     * Logic Check: Does the system throw the correct "Not Found" error?
     */
    @Test
    void shouldThrowException_WhenSweetNotFoundForUpdate() {
        // 1. Script: "If asked for ID 99, say it is EMPTY (doesn't exist)."
        when(sweetRepository.findById(99L)).thenReturn(Optional.empty());

        // Prepare some dummy data for the update.
        SweetRequestDto dto = new SweetRequestDto("Name", "Cat", 10.0, 10);

        // 2. Act & Assert:
        // We expect the service to throw 'SweetNotFoundException' when we try to update ID 99.
        assertThrows(SweetNotFoundException.class, () -> sweetService.updateSweet(99L, dto));
    }

    /**
     * Test Case 2: Searching for sweets.
     * Logic Check: Does the search function pass the data correctly and return the list?
     */
    @Test
    void shouldSearchSweetsCorrectly() {
        // 1. Arrange: Create a sweet that should be found.
        Sweet sweet = new Sweet(1L, "Kaju Katli", "Cashew", 20.0, 10);

        // Script: "If asked to search for 'Kaju', return a list containing this sweet."
        // We pass 'null' for price ranges because we aren't testing those right now.
        when(sweetRepository.searchSweets("Kaju", null, null)).thenReturn(List.of(sweet));

        // 2. Act: Call the service's search method.
        List<Sweet> results = sweetService.searchSweets("Kaju", null, null);

        // 3. Assert: Verify the results.
        // Did we get exactly 1 item back?
        assertEquals(1, results.size());
        // Is the name of the item correct?
        assertEquals("Kaju Katli", results.get(0).getName());
    }
}