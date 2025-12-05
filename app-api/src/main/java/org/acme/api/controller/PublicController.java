package org.acme.api.controller;

import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;

/**
 * Public controller for unauthenticated endpoints
 */
@RestController
@RequestMapping("/api/public")
public class PublicController {

    @GetMapping(value = "/health", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<Map<String, String>> health() {
        return Mono.just(Map.of("status", "UP", "message", "Service is healthy"));
    }

    @GetMapping(value = "/info", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<Map<String, String>> info() {
        return Mono.just(Map.of(
                "name", "Spring WebFlux Security Application",
                "version", "1.0.0",
                "description", "Multi-module Spring WebFlux application with security"));
    }
}
