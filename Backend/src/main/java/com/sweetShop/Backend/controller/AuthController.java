package com.sweetShop.Backend.controller;

import com.sweetShop.Backend.dto.AuthRequestDto;
import com.sweetShop.Backend.dto.AuthResponseDto;
import com.sweetShop.Backend.service.AuthService;
import com.sweetShop.Backend.util.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<String>> register(@RequestBody AuthRequestDto dto) {
        String result = authService.register(dto);
        // Wrap the result string in our response object
        return new ResponseEntity<>(new ApiResponse<>(true, result, null), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponseDto>> login(@RequestBody AuthRequestDto dto) {
        AuthResponseDto response = authService.login(dto);
        // Wrap the token DTO in our response object
        return ResponseEntity.ok(new ApiResponse<>(true, "Login Successful", response));
    }
}