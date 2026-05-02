package com.example.userservice.interfaces.controller;

import com.example.userservice.application.dto.CreateUserRequest;
import com.example.userservice.application.dto.LoginRequest;
import com.example.userservice.application.dto.LoginResponse;
import com.example.userservice.application.dto.UserResponse;
import com.example.userservice.application.usecase.CreateUserUseCase;
import com.example.userservice.application.usecase.LoginUseCase;
import com.example.userservice.infrastructure.config.ApiResult;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final LoginUseCase loginUseCase;
    private final CreateUserUseCase createUserUseCase;

    public AuthController(LoginUseCase loginUseCase, CreateUserUseCase createUserUseCase) {
        this.loginUseCase = loginUseCase;
        this.createUserUseCase = createUserUseCase;
    }

    @PostMapping("/login")
    public ApiResult<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ApiResult.success(loginUseCase.execute(request));
    }

    @PostMapping("/register")
    public ApiResult<UserResponse> register(@Valid @RequestBody CreateUserRequest request) {
        return ApiResult.success(createUserUseCase.execute(request));
    }
}