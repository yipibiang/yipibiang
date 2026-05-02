package com.example.userservice.interfaces.controller;

import com.example.userservice.application.dto.UpdateUserRequest;
import com.example.userservice.application.dto.UserResponse;
import com.example.userservice.application.usecase.DeleteUserUseCase;
import com.example.userservice.application.usecase.GetUserByIdUseCase;
import com.example.userservice.application.usecase.UpdateUserUseCase;
import com.example.userservice.infrastructure.config.ApiResult;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final GetUserByIdUseCase getUserByIdUseCase;
    private final UpdateUserUseCase updateUserUseCase;
    private final DeleteUserUseCase deleteUserUseCase;

    public UserController(GetUserByIdUseCase getUserByIdUseCase, UpdateUserUseCase updateUserUseCase, DeleteUserUseCase deleteUserUseCase) {
        this.getUserByIdUseCase = getUserByIdUseCase;
        this.updateUserUseCase = updateUserUseCase;
        this.deleteUserUseCase = deleteUserUseCase;
    }

    @GetMapping("/{id}")
    public ApiResult<UserResponse> getById(@PathVariable Long id) {
        return ApiResult.success(getUserByIdUseCase.execute(id));
    }

    @PutMapping("/{id}")
    public ApiResult<UserResponse> update(@PathVariable Long id, @Valid @RequestBody UpdateUserRequest request) {
        return ApiResult.success(updateUserUseCase.execute(id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResult<Void> delete(@PathVariable Long id) {
        deleteUserUseCase.execute(id);
        return ApiResult.success(null);
    }
}