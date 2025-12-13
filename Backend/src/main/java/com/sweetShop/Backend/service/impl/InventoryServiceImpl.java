package com.sweetShop.Backend.service.impl;

import com.sweetShop.Backend.exception.SweetNotFoundException;
import com.sweetShop.Backend.model.Sweet;
import com.sweetShop.Backend.repository.SweetRepository;
import com.sweetShop.Backend.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private final SweetRepository sweetRepository;

    @Override
    @Transactional
    public Sweet purchaseSweet(Long sweetId) {
        Sweet sweet = sweetRepository.findById(sweetId)
                .orElseThrow(() -> new SweetNotFoundException("Sweet not found with id: " + sweetId));

        if (sweet.getQuantity() <= 0) {
            throw new RuntimeException("Out of Stock");
        }

        sweet.setQuantity(sweet.getQuantity() - 1);
        return sweetRepository.save(sweet);
    }

    @Override
    @Transactional
    public Sweet restockSweet(Long sweetId, Integer amount) {
        if (amount == null || amount <= 0) {
            throw new RuntimeException("Restock amount must be greater than zero");
        }

        Sweet sweet = sweetRepository.findById(sweetId)
                .orElseThrow(() -> new SweetNotFoundException("Sweet not found with id: " + sweetId));

        sweet.setQuantity(sweet.getQuantity() + amount);
        return sweetRepository.save(sweet);
    }
}