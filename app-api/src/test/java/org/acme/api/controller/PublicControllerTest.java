package org.acme.api.controller;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

@WebFluxTest(controllers = PublicController.class, excludeAutoConfiguration = {
        org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration.class
})
class PublicControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void health_ShouldReturnHealthStatus() {
        webTestClient.get()
                .uri("/api/public/health")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(Map.class)
                .value(map -> {
                    assert map.get("status").equals("UP");
                    assert map.get("message").equals("Service is healthy");
                });
    }

    @Test
    void info_ShouldReturnApplicationInfo() {
        webTestClient.get()
                .uri("/api/public/info")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(Map.class)
                .value(map -> {
                    assert map.get("name").equals("Spring WebFlux Security Application");
                    assert map.get("version").equals("1.0.0");
                    assert map.containsValue("Multi-module Spring WebFlux application with security");
                });
    }
}
