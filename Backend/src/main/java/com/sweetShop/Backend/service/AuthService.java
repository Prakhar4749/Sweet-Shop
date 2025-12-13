package com.sweetShop.Backend.service;

import com.sweetShop.Backend.dto.AuthRequestDto;
import com.sweetShop.Backend.dto.AuthResponseDto;

public interface AuthService {

    String register(AuthRequestDto dto);

    AuthResponseDto login(AuthRequestDto dto);
}
