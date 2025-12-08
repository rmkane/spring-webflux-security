package org.acme.api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.acme.api.dto.ProductRequest;
import org.acme.api.dto.ProductResponse;
import org.acme.api.service.ProductService;

/**
 * Reactive REST controller for Product operations
 */
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping(produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_NDJSON_VALUE })
    public Flux<ProductResponse> getAllProducts() {
        return productService.getAllProducts();
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ProductResponse> getProductById(@PathVariable("id") @NonNull Long id) {
        return productService.getProductById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ProductResponse> createProduct(@RequestBody ProductRequest request) {
        return productService.createProduct(request);
    }

    @PutMapping("/{id}")
    public Mono<ProductResponse> updateProduct(@PathVariable("id") @NonNull Long id, @RequestBody ProductRequest request) {
        return productService.updateProduct(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteProduct(@PathVariable("id") @NonNull Long id) {
        return productService.deleteProduct(id);
    }
}
