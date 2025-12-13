package com.sweetShop.Backend.service;

import com.sweetShop.Backend.dto.SweetRequestDto;
import com.sweetShop.Backend.model.Sweet;

import java.util.List;

public interface SweetService {

    Sweet addSweet(SweetRequestDto dto);

    List<Sweet> getAllSweets();

    Sweet getSweetById(Long id);

    Sweet updateSweet(Long id, SweetRequestDto dto);

    void deleteSweet(Long id);

    List<Sweet> searchSweets(String query, Double min, Double max);
}
