package com.sweetShop.Backend.service.impl;

import com.sweetShop.Backend.dto.SweetRequestDto;
import com.sweetShop.Backend.exception.SweetNotFoundException;
import com.sweetShop.Backend.model.Sweet;
import com.sweetShop.Backend.repository.SweetRepository;
import com.sweetShop.Backend.service.SweetService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SweetServiceImpl implements SweetService {

    private final SweetRepository sweetRepository;

    @Override
    public Sweet addSweet(SweetRequestDto dto) {
        if (sweetRepository.findByName(dto.getName()).isPresent()) {
            throw new IllegalArgumentException("Sweet with this name already exists");
        }
        Sweet sweet = new Sweet(null, dto.getName(), dto.getCategory(), dto.getPrice(), dto.getQuantity());
        return sweetRepository.save(sweet);
    }

    @Override
    public List<Sweet> getAllSweets() {
        return sweetRepository.findAll();
    }

    @Override
    public Sweet getSweetById(Long id) {
        return sweetRepository.findById(id)
                .orElseThrow(() -> new SweetNotFoundException("Sweet not found with id: " + id));
    }

    @Override
    public Sweet updateSweet(Long id, SweetRequestDto dto) {
        Sweet sweet = getSweetById(id); // Re-use get method for DRY code
        sweet.setName(dto.getName());
        sweet.setCategory(dto.getCategory());
        sweet.setPrice(dto.getPrice());
        sweet.setQuantity(dto.getQuantity());
        return sweetRepository.save(sweet);
    }

    @Override
    public void deleteSweet(Long id) {
        if (!sweetRepository.existsById(id)) {
            throw new SweetNotFoundException("Cannot delete. Sweet not found.");
        }
        sweetRepository.deleteById(id);
    }

    @Override
    public List<Sweet> searchSweets(String query, Double min, Double max) {
        return sweetRepository.searchSweets(query, min, max);
    }
}