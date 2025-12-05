package org.acme.api.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.acme.api.config.TestSecurityConfig;
import org.acme.api.dto.UserRequest;
import org.acme.api.dto.UserResponse;
import org.acme.api.service.UserService;

@WebFluxTest(controllers = UserController.class, excludeAutoConfiguration = {
        org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration.class
})
@Import(TestSecurityConfig.class)
class UserControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private UserService userService;

    @Test
    void getAllUsers_ShouldReturnUsers() {
        UserResponse user1 = new UserResponse(1L, "user1", "user1@example.com", "John", "Doe",
                LocalDateTime.now(), LocalDateTime.now());
        UserResponse user2 = new UserResponse(2L, "user2", "user2@example.com", "Jane", "Smith",
                LocalDateTime.now(), LocalDateTime.now());

        when(userService.getAllUsers()).thenReturn(Flux.just(user1, user2));

        webTestClient.get()
                .uri("/api/users")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(UserResponse.class)
                .hasSize(2);
    }

    @Test
    void getUserById_ShouldReturnUser() {
        Long userId = 1L;
        UserResponse user = new UserResponse(userId, "user1", "user1@example.com", "John", "Doe",
                LocalDateTime.now(), LocalDateTime.now());

        when(userService.getUserById(userId)).thenReturn(Mono.just(user));

        webTestClient.get()
                .uri("/api/users/{id}", userId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(UserResponse.class)
                .isEqualTo(user);
    }

    @Test
    void createUser_ShouldReturnCreatedUser() {
        UserRequest request = new UserRequest("user1", "user1@example.com", "John", "Doe");
        UserResponse response = new UserResponse(1L, "user1", "user1@example.com", "John", "Doe",
                LocalDateTime.now(), LocalDateTime.now());

        when(userService.createUser(any(UserRequest.class))).thenReturn(Mono.just(response));

        webTestClient.post()
                .uri("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(UserResponse.class)
                .isEqualTo(response);
    }

    @Test
    void updateUser_ShouldReturnUpdatedUser() {
        Long userId = 1L;
        UserRequest request = new UserRequest("user1", "user1@example.com", "John", "Updated");
        UserResponse response = new UserResponse(userId, "user1", "user1@example.com", "John", "Updated",
                LocalDateTime.now(), LocalDateTime.now());

        when(userService.updateUser(eq(userId), any(UserRequest.class))).thenReturn(Mono.just(response));

        webTestClient.put()
                .uri("/api/users/{id}", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(UserResponse.class)
                .isEqualTo(response);
    }

    @Test
    void deleteUser_ShouldReturnNoContent() {
        Long userId = 1L;

        when(userService.deleteUser(userId)).thenReturn(Mono.empty());

        webTestClient.delete()
                .uri("/api/users/{id}", userId)
                .exchange()
                .expectStatus().isNoContent();
    }
}
