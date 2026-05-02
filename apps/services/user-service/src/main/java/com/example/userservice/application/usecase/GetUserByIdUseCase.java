package com.example.userservice.application.usecase;

import com.example.userservice.application.dto.UserResponse;

public interface GetUserByIdUseCase {
    UserResponse execute(Long id);
}