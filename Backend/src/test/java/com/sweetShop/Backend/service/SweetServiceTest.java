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

@ExtendWith(MockitoExtension.class)
class SweetServiceTest {

    @Mock private SweetRepository sweetRepository;
    @InjectMocks private SweetServiceImpl sweetService;

    @Test
    void shouldThrowException_WhenSweetNotFoundForUpdate() {
        when(sweetRepository.findById(99L)).thenReturn(Optional.empty());
        SweetRequestDto dto = new SweetRequestDto("Name", "Cat", 10.0, 10);

        assertThrows(SweetNotFoundException.class, () -> sweetService.updateSweet(99L, dto));
    }

    @Test
    void shouldSearchSweetsCorrectly() {
        Sweet sweet = new Sweet(1L, "Kaju Katli", "Cashew", 20.0, 10);
        when(sweetRepository.searchSweets("Kaju", null, null)).thenReturn(List.of(sweet));

        List<Sweet> results = sweetService.searchSweets("Kaju", null, null);
        assertEquals(1, results.size());
        assertEquals("Kaju Katli", results.get(0).getName());
    }
}