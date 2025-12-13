package com.sweetShop.Backend.service.impl;

import com.sweetShop.Backend.dto.SweetRequestDto;
import com.sweetShop.Backend.exception.SweetNotFoundException;
import com.sweetShop.Backend.model.Sweet;
import com.sweetShop.Backend.repository.SweetRepository;
import com.sweetShop.Backend.service.SweetService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * The Sweet Service Implementation.
 * This class handles the core logic for managing the Sweet menu.
 * It is responsible for creating, reading, updating, and deleting sweets (CRUD).
 */
@Service // Tells Spring: "This is a Service class containing business logic."
@RequiredArgsConstructor
public class SweetServiceImpl implements SweetService {

    // Access to the database.
    private final SweetRepository sweetRepository;

    /**
     * Logic to add a new sweet to the menu.
     * @param dto The data from the user's "New Product Form".
     * @return The saved sweet.
     */
    @Override
    public Sweet addSweet(SweetRequestDto dto) {
        // 1. Duplicate Check: Before we add anything, check if this name exists.
        // We don't want two different items named "Laddu".
        if (sweetRepository.findByName(dto.getName()).isPresent()) {
            throw new IllegalArgumentException("Sweet with this name already exists");
        }

        // 2. Convert DTO to Entity.
        // Copy the data from the temporary Form (DTO) to the permanent Database Object (Sweet).
        Sweet sweet = new Sweet(null, dto.getName(), dto.getCategory(), dto.getPrice(), dto.getQuantity());

        // 3. Save it to the database.
        return sweetRepository.save(sweet);
    }

    /**
     * Logic to get the entire menu.
     * @return A list of all sweets.
     */
    @Override
    public List<Sweet> getAllSweets() {
        // Simple pass-through: Ask the repository for everything.
        return sweetRepository.findAll();
    }

    /**
     * Logic to find a specific sweet.
     * @param id The ID number.
     * @return The sweet if found, or an error if not.
     */
    @Override
    public Sweet getSweetById(Long id) {
        return sweetRepository.findById(id)
                // If the box is empty (Optional.empty), throw our custom "Not Found" error.
                .orElseThrow(() -> new SweetNotFoundException("Sweet not found with id: " + id));
    }

    /**
     * Logic to update an existing sweet.
     * @param id The ID of the sweet to change.
     * @param dto The new data (some fields might be blank if we are only updating one thing).
     * @return The updated sweet.
     */
    @Override
    public Sweet updateSweet(Long id, SweetRequestDto dto) {
        // 1. Fetch the existing sweet first. We need to make sure it exists.
        Sweet sweet = getSweetById(id);

        // 2. Selective Update (Patching).
        // We only update a field if the user provided a new value.
        // If they sent 'null' for the name, we keep the OLD name.
        if (dto.getName() != null) {
            sweet.setName(dto.getName());
        }

        if (dto.getCategory() != null) {
            sweet.setCategory(dto.getCategory());
        }

        if (dto.getPrice() != null) {
            sweet.setPrice(dto.getPrice());
        }

        if (dto.getQuantity() != null) {
            sweet.setQuantity(dto.getQuantity());
        }

        // 3. Save the changes back to the database.
        return sweetRepository.save(sweet);
    }

    /**
     * Logic to remove a sweet from the menu.
     * @param id The ID to delete.
     */
    @Override
    public void deleteSweet(Long id) {
        // 1. Safety Check: Does this sweet even exist?
        // You can't delete a ghost.
        if (!sweetRepository.existsById(id)) {
            throw new SweetNotFoundException("Cannot delete. Sweet not found.");
        }

        // 2. Perform the delete.
        sweetRepository.deleteById(id);
    }

    /**
     * Logic to search for sweets using filters.
     * @param query Text to search for (Name/Category).
     * @param min Minimum price.
     * @param max Maximum price.
     * @return A list of matching sweets.
     */
    @Override
    public List<Sweet> searchSweets(String query, Double min, Double max) {
        // Pass all the filters straight to our custom Repository query.
        return sweetRepository.searchSweets(query, min, max);
    }
}