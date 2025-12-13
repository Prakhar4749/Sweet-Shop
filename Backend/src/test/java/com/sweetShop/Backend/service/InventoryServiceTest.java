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

@ExtendWith(MockitoExtension.class)
class InventoryServiceTest {

    @Mock private SweetRepository sweetRepository;
    @InjectMocks private InventoryServiceImpl inventoryService;

    @Test
    void shouldPurchaseSweetSuccessfully() {
        Sweet sweet = new Sweet(1L, "Barfi", "Milk", 10.0, 10);
        when(sweetRepository.findById(1L)).thenReturn(Optional.of(sweet));
        when(sweetRepository.save(any(Sweet.class))).thenReturn(sweet);

        Sweet result = inventoryService.purchaseSweet(1L);

        assertEquals(9, result.getQuantity()); // Stock should decrease
        verify(sweetRepository).save(sweet);
    }

    @Test
    void shouldThrowException_WhenOutOfStock() {
        Sweet sweet = new Sweet(1L, "Barfi", "Milk", 10.0, 0); // 0 Quantity
        when(sweetRepository.findById(1L)).thenReturn(Optional.of(sweet));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> inventoryService.purchaseSweet(1L));
        assertEquals("Out of Stock", ex.getMessage());
    }

    @Test
    void shouldRestockSweetSuccessfully() {
        Sweet sweet = new Sweet(1L, "Barfi", "Milk", 10.0, 10);
        when(sweetRepository.findById(1L)).thenReturn(Optional.of(sweet));
        when(sweetRepository.save(any(Sweet.class))).thenReturn(sweet);

        Sweet result = inventoryService.restockSweet(1L, 50);

        assertEquals(60, result.getQuantity()); // 10 + 50 = 60
    }
}