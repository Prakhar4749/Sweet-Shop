package com.sweetShop.Backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SweetRequestDto {
    private String name;
    private String category;
    private Double price;
    private Integer quantity;
}