package com.example.userservice.application.usecase.impl;

import com.example.userservice.application.dto.UserResponse;
import com.example.userservice.application.usecase.GetUserByIdUseCase;
import com.example.userservice.domain.User;
import com.example.userservice.domain.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class GetUserByIdUseCaseImpl implements GetUserByIdUseCase {

    private final UserRepository userRepository;

    public GetUserByIdUseCaseImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserResponse execute(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found: " + id));
        return toResponse(user);
    }

    private UserResponse toResponse(User user) {
        UserResponse resp = new UserResponse();
        resp.setId(user.getId());
        resp.setUsername(user.getUsername());
        resp.setEmail(user.getEmail());
        resp.setPhone(user.getPhone());
        resp.setNickname(user.getNickname());
        resp.setStatus(user.getStatus());
        resp.setCreatedAt(user.getCreatedAt());
        resp.setUpdatedAt(user.getUpdatedAt());
        return resp;
    }
}