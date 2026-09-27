package com.enerpulse.controller;

import com.enerpulse.common.api.ApiResponse;
import com.enerpulse.dto.request.LoginRequest;
import com.enerpulse.dto.response.LoginResponse;
import com.enerpulse.entity.User;
import com.enerpulse.security.SecurityUtils;
import com.enerpulse.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.success(authService.login(request));
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout() {
        return ApiResponse.success();
    }

    @GetMapping("/me")
    public ApiResponse<User> me() {
        Long userId = SecurityUtils.getCurrentUserId();
        return ApiResponse.success(authService.getCurrentUser(userId));
    }
}
