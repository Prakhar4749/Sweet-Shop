package com.sweetShop.Backend.service;

import com.sweetShop.Backend.model.Sweet;

public interface InventoryService {
    Sweet purchaseSweet(Long sweetId);
    Sweet restockSweet(Long sweetId, Integer amount);
}