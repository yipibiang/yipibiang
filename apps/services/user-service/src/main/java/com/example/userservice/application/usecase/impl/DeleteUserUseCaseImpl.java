package com.example.userservice.application.usecase.impl;

import com.example.userservice.application.usecase.DeleteUserUseCase;
import com.example.userservice.domain.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class DeleteUserUseCaseImpl implements DeleteUserUseCase {

    private final UserRepository userRepository;

    public DeleteUserUseCaseImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void execute(Long id) {
        userRepository.deleteById(id);
    }
}