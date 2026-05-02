package com.example.userservice.application.usecase;

import com.example.userservice.application.dto.UpdateUserRequest;
import com.example.userservice.application.dto.UserResponse;

public interface UpdateUserUseCase {
    UserResponse execute(Long id, UpdateUserRequest request);
}