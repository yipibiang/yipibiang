package com.example.userservice.infrastructure.persistence;

import com.example.userservice.domain.User;
import com.example.userservice.domain.UserRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class UserRepositoryImpl implements UserRepository {

    private final UserMapper userMapper;

    public UserRepositoryImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public User save(User user) {
        userMapper.insert(user);
        return user;
    }

    @Override
    public Optional<User> findById(Long id) {
        return Optional.ofNullable(userMapper.findById(id));
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return Optional.ofNullable(userMapper.findByUsername(username));
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return Optional.ofNullable(userMapper.findByEmail(email));
    }

    @Override
    public List<User> findAll() {
        return userMapper.findAll();
    }

    @Override
    public User update(User user) {
        userMapper.update(user);
        return user;
    }

    @Override
    public void deleteById(Long id) {
        userMapper.deleteById(id);
    }
}