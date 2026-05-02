package com.example.userservice.application.usecase;

import com.example.userservice.application.dto.LoginRequest;
import com.example.userservice.application.dto.LoginResponse;

public interface LoginUseCase {
    LoginResponse execute(LoginRequest request);
}