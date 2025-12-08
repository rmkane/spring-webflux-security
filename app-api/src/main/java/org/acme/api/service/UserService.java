package org.acme.api.service;

import org.springframework.lang.NonNull;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.acme.api.dto.UserRequest;
import org.acme.api.dto.UserResponse;

/**
 * User service layer
 */
public interface UserService {

    Flux<UserResponse> getAllUsers();

    Mono<UserResponse> getUserById(@NonNull Long id);

    Mono<UserResponse> createUser(UserRequest request);

    Mono<UserResponse> updateUser(@NonNull Long id, UserRequest request);

    Mono<Void> deleteUser(@NonNull Long id);
}
