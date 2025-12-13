package com.sweetShop.Backend.service.impl;

import com.sweetShop.Backend.exception.SweetNotFoundException;
import com.sweetShop.Backend.model.Sweet;
import com.sweetShop.Backend.repository.SweetRepository;
import com.sweetShop.Backend.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * The Inventory Service Implementation.
 * This class handles the business logic for changing stock levels.
 * It ensures we never sell something we don't have.
 */
@Service // Marks this as a Service worker for Spring.
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    // Access to the database to find and save sweet records.
    private final SweetRepository sweetRepository;

    /**
     * Logic for buying a sweet.
     * @param sweetId The ID of the sweet the user wants to buy.
     * @return The updated sweet record (with the new lower quantity).
     */
    @Override
    @Transactional // "All or Nothing." If an error happens here, the database rolls back to the previous state.
    public Sweet purchaseSweet(Long sweetId) {

        // 1. Find the sweet in the database.
        // If it doesn't exist, stop immediately.
        Sweet sweet = sweetRepository.findById(sweetId)
                .orElseThrow(() -> new SweetNotFoundException("Sweet not found with id: " + sweetId));

        // 2. Check the Stock Level.
        // If we have 0 (or less), we cannot sell it. Throw an error.
        if (sweet.getQuantity() <= 0) {
            throw new RuntimeException("Out of Stock");
        }

        // 3. Decrease the quantity by 1.
        sweet.setQuantity(sweet.getQuantity() - 1);

        // 4. Save the new quantity back to the database.
        return sweetRepository.save(sweet);
    }

    /**
     * Logic for restocking (adding more inventory).
     * @param sweetId The ID of the sweet being restocked.
     * @param amount How many new items are being added.
     * @return The updated sweet record (with the new higher quantity).
     */
    @Override
    @Transactional
    public Sweet restockSweet(Long sweetId, Integer amount) {

        // 1. Validation check: You can't restock 0 or negative items.
        if (amount == null || amount <= 0) {
            throw new RuntimeException("Restock amount must be greater than zero");
        }

        // 2. Find the sweet in the database.
        Sweet sweet = sweetRepository.findById(sweetId)
                .orElseThrow(() -> new SweetNotFoundException("Sweet not found with id: " + sweetId));

        // 3. Add the new amount to the existing quantity.
        sweet.setQuantity(sweet.getQuantity() + amount);

        // 4. Save the new total back to the database.
        return sweetRepository.save(sweet);
    }
}