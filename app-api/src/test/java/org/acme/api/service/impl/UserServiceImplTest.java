package org.acme.api.service.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import org.acme.api.dto.UserRequest;
import org.acme.api.dto.UserResponse;
import org.acme.api.mapper.UserMapper;
import org.acme.api.service.UserService;
import org.acme.persistence.model.User;
import org.acme.persistence.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userServiceImpl;

    private UserService userService;

    private User user;
    private UserResponse userResponse;
    private UserRequest userRequest;

    @BeforeEach
    void setUp() {
        LocalDateTime now = LocalDateTime.now();
        user = new User(1L, "user1", "user1@example.com", "John", "Doe", now, now);
        userResponse = new UserResponse(1L, "user1", "user1@example.com", "John", "Doe", now, now);
        userRequest = new UserRequest("user1", "user1@example.com", "John", "Doe");
        userService = userServiceImpl;
    }

    @Test
    void getAllUsers_ShouldReturnAllUsers() {
        when(userRepository.findAll()).thenReturn(Flux.just(user));
        when(userMapper.toResponse(user)).thenReturn(userResponse);

        StepVerifier.create(userService.getAllUsers())
                .expectNext(userResponse)
                .verifyComplete();

        verify(userRepository).findAll();
        verify(userMapper).toResponse(user);
    }

    @Test
    void getUserById_ShouldReturnUser() {
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Mono.just(user));
        when(userMapper.toResponse(user)).thenReturn(userResponse);

        StepVerifier.create(userService.getUserById(userId))
                .expectNext(userResponse)
                .verifyComplete();

        verify(userRepository).findById(userId);
        verify(userMapper).toResponse(user);
    }

    @Test
    void createUser_ShouldReturnCreatedUser() {
        when(userMapper.toEntity(userRequest)).thenReturn(user);
        when(userRepository.save(any(User.class))).thenReturn(Mono.just(user));
        when(userMapper.toResponse(user)).thenReturn(userResponse);

        StepVerifier.create(userService.createUser(userRequest))
                .expectNext(userResponse)
                .verifyComplete();

        verify(userMapper).toEntity(userRequest);
        verify(userRepository).save(any(User.class));
        verify(userMapper).toResponse(user);
    }

    @Test
    void updateUser_ShouldReturnUpdatedUser() {
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Mono.just(user));
        when(userRepository.save(any(User.class))).thenReturn(Mono.just(user));
        when(userMapper.toResponse(user)).thenReturn(userResponse);

        StepVerifier.create(userService.updateUser(userId, userRequest))
                .expectNext(userResponse)
                .verifyComplete();

        verify(userRepository).findById(userId);
        verify(userMapper).updateEntityFromRequest(userRequest, user);
        verify(userRepository).save(any(User.class));
        verify(userMapper).toResponse(user);
    }

    @Test
    void deleteUser_ShouldDeleteUser() {
        Long userId = 1L;
        when(userRepository.deleteById(userId)).thenReturn(Mono.empty());

        StepVerifier.create(userService.deleteUser(userId))
                .verifyComplete();

        verify(userRepository).deleteById(userId);
    }
}
