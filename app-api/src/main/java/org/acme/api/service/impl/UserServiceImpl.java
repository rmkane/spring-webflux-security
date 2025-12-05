package org.acme.api.service.impl;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.acme.api.dto.UserRequest;
import org.acme.api.dto.UserResponse;
import org.acme.api.mapper.UserMapper;
import org.acme.api.service.UserService;
import org.acme.persistence.model.User;
import org.acme.persistence.repository.UserRepository;

/**
 * User service implementation
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public Flux<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .map(userMapper::toResponse);
    }

    @Override
    public Mono<UserResponse> getUserById(Long id) {
        return userRepository.findById(id)
                .map(userMapper::toResponse);
    }

    @Override
    public Mono<UserResponse> createUser(UserRequest request) {
        User user = userMapper.toEntity(request);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        return userRepository.save(user)
                .map(userMapper::toResponse);
    }

    @Override
    public Mono<UserResponse> updateUser(Long id, UserRequest request) {
        return userRepository.findById(id)
                .flatMap(existingUser -> {
                    userMapper.updateEntityFromRequest(request, existingUser);
                    existingUser.setUpdatedAt(LocalDateTime.now());
                    return userRepository.save(existingUser);
                })
                .map(userMapper::toResponse);
    }

    @Override
    public Mono<Void> deleteUser(Long id) {
        return userRepository.deleteById(id);
    }
}
