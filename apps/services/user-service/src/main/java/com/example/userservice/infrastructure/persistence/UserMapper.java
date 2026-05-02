package com.example.userservice.infrastructure.persistence;

import com.example.userservice.domain.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {
    void insert(User user);
    User findById(Long id);
    User findByUsername(String username);
    User findByEmail(String email);
    java.util.List<User> findAll();
    void update(User user);
    void deleteById(Long id);
}