package com.example.userservice.application.usecase.impl;

import com.example.userservice.application.dto.UpdateUserRequest;
import com.example.userservice.application.dto.UserResponse;
import com.example.userservice.application.usecase.UpdateUserUseCase;
import com.example.userservice.domain.User;
import com.example.userservice.domain.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UpdateUserUseCaseImpl implements UpdateUserUseCase {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UpdateUserUseCaseImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserResponse execute(Long id, UpdateUserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found: " + id));

        if (request.getEmail() != null) user.setEmail(request.getEmail());
        if (request.getPhone() != null) user.setPhone(request.getPhone());
        if (request.getNickname() != null) user.setNickname(request.getNickname());
        if (request.getPassword() != null) user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setUpdatedAt(java.time.LocalDateTime.now());

        User updated = userRepository.update(user);
        return toResponse(updated);
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