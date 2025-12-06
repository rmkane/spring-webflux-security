package org.acme.api.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.acme.api.config.TestSecurityConfig;
import org.acme.api.dto.ProductRequest;
import org.acme.api.dto.ProductResponse;
import org.acme.api.service.ProductService;

@WebFluxTest(controllers = ProductController.class, excludeAutoConfiguration = {
        ReactiveSecurityAutoConfiguration.class
})
@Import({ TestSecurityConfig.class, ProductControllerTest.TestConfig.class })
class ProductControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private ProductService productService;

    @TestConfiguration
    static class TestConfig {
        @Bean
        @Primary
        ProductService productService() {
            return mock(ProductService.class);
        }
    }

    @Test
    void getAllProducts_ShouldReturnProducts() {
        ProductResponse product1 = new ProductResponse(1L, "Product 1", "Description 1", BigDecimal.valueOf(10.99), 100,
                LocalDateTime.now(), LocalDateTime.now());
        ProductResponse product2 = new ProductResponse(2L, "Product 2", "Description 2", BigDecimal.valueOf(20.99), 200,
                LocalDateTime.now(), LocalDateTime.now());

        when(productService.getAllProducts()).thenReturn(Flux.just(product1, product2));

        webTestClient.get()
                .uri("/api/products")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(ProductResponse.class)
                .hasSize(2);
    }

    @Test
    void getProductById_ShouldReturnProduct() {
        Long productId = 1L;
        ProductResponse product = new ProductResponse(productId, "Product 1", "Description 1",
                BigDecimal.valueOf(10.99), 100,
                LocalDateTime.now(), LocalDateTime.now());

        when(productService.getProductById(productId)).thenReturn(Mono.just(product));

        webTestClient.get()
                .uri("/api/products/{id}", productId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(ProductResponse.class)
                .isEqualTo(product);
    }

    @Test
    void createProduct_ShouldReturnCreatedProduct() {
        ProductRequest request = new ProductRequest("Product 1", "Description 1", BigDecimal.valueOf(10.99), 100);
        ProductResponse response = new ProductResponse(1L, "Product 1", "Description 1", BigDecimal.valueOf(10.99), 100,
                LocalDateTime.now(), LocalDateTime.now());

        when(productService.createProduct(any(ProductRequest.class))).thenReturn(Mono.just(response));

        webTestClient.post()
                .uri("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(ProductResponse.class)
                .isEqualTo(response);
    }

    @Test
    void updateProduct_ShouldReturnUpdatedProduct() {
        Long productId = 1L;
        ProductRequest request = new ProductRequest("Updated Product", "Updated Description", BigDecimal.valueOf(15.99),
                150);
        ProductResponse response = new ProductResponse(productId, "Updated Product", "Updated Description",
                BigDecimal.valueOf(15.99), 150,
                LocalDateTime.now(), LocalDateTime.now());

        when(productService.updateProduct(eq(productId), any(ProductRequest.class))).thenReturn(Mono.just(response));

        webTestClient.put()
                .uri("/api/products/{id}", productId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(ProductResponse.class)
                .isEqualTo(response);
    }

    @Test
    void deleteProduct_ShouldReturnNoContent() {
        Long productId = 1L;

        when(productService.deleteProduct(productId)).thenReturn(Mono.empty());

        webTestClient.delete()
                .uri("/api/products/{id}", productId)
                .exchange()
                .expectStatus().isNoContent();
    }
}
