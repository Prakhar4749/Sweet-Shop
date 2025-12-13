package com.sweetShop.Backend.service;

import com.sweetShop.Backend.exception.SweetNotFoundException;
import com.sweetShop.Backend.model.Sweet;
import com.sweetShop.Backend.repository.SweetRepository;
import com.sweetShop.Backend.service.impl.InventoryServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Inventory Service Test.
 * This class tests the math and logic of buying and restocking.
 * It ensures our inventory numbers never get messed up.
 */
@ExtendWith(MockitoExtension.class) // Enables the "Mocking" laboratory.
class InventoryServiceTest {

    // The Fake Database. We control what sweets are "inside" it.
    @Mock private SweetRepository sweetRepository;

    // The Real Service we are testing. It uses the fake database above.
    @InjectMocks private InventoryServiceImpl inventoryService;

    /**
     * Test Case 1: Buying something when we have enough stock.
     * Logic Check: Does the quantity go DOWN by exactly 1?
     */
    @Test
    void shouldPurchaseSweetSuccessfully() {
        // 1. Arrange: Create a sweet with 10 items in stock.
        Sweet sweet = new Sweet(1L, "Barfi", "Milk", 10.0, 10);

        // Script the fake DB: "If asked for item #1, give them this sweet."
        when(sweetRepository.findById(1L)).thenReturn(Optional.of(sweet));

        // Script the fake DB: "If asked to save, just return the saved object."
        when(sweetRepository.save(any(Sweet.class))).thenReturn(sweet);

        // 2. Act: Run the purchase method.
        Sweet result = inventoryService.purchaseSweet(1L);

        // 3. Assert: Verify the math.
        // It started with 10. After purchase, it should be 9.
        assertEquals(9, result.getQuantity());

        // Verify: Did we actually write this new number back to the database?
        verify(sweetRepository).save(sweet);
    }

    /**
     * Test Case 2: Buying something when shelves are empty.
     * Logic Check: Does the system throw an error instead of letting us go into negative numbers?
     */
    @Test
    void shouldThrowException_WhenOutOfStock() {
        // 1. Arrange: Create a sweet with 0 items.
        Sweet sweet = new Sweet(1L, "Barfi", "Milk", 10.0, 0); // 0 Quantity

        // Script the fake DB to return this empty sweet.
        when(sweetRepository.findById(1L)).thenReturn(Optional.of(sweet));

        // 2. Act & Assert:
        // We expect a RuntimeException to happen when we call purchaseSweet.
        RuntimeException ex = assertThrows(RuntimeException.class, () -> inventoryService.purchaseSweet(1L));

        // Verify the error message is exactly what we expect.
        assertEquals("Out of Stock", ex.getMessage());
    }

    /**
     * Test Case 3: Restocking (Adding inventory).
     * Logic Check: Does the quantity go UP correctly?
     */
    @Test
    void shouldRestockSweetSuccessfully() {
        // 1. Arrange: Start with 10 items.
        Sweet sweet = new Sweet(1L, "Barfi", "Milk", 10.0, 10);

        when(sweetRepository.findById(1L)).thenReturn(Optional.of(sweet));
        when(sweetRepository.save(any(Sweet.class))).thenReturn(sweet);

        // 2. Act: Add 50 new items.
        Sweet result = inventoryService.restockSweet(1L, 50);

        // 3. Assert: 10 + 50 should equal 60.
        assertEquals(60, result.getQuantity());
    }
}