package com.example.userservice.application.usecase;

import com.example.userservice.application.dto.CreateUserRequest;
import com.example.userservice.application.dto.UserResponse;

public interface CreateUserUseCase {
    UserResponse execute(CreateUserRequest request);
}