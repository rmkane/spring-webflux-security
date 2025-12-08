package org.acme.api.service;

import org.springframework.lang.NonNull;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.acme.api.dto.ProductRequest;
import org.acme.api.dto.ProductResponse;

/**
 * Product service layer
 */
public interface ProductService {

    Flux<ProductResponse> getAllProducts();

    Mono<ProductResponse> getProductById(@NonNull Long id);

    Mono<ProductResponse> createProduct(ProductRequest request);

    Mono<ProductResponse> updateProduct(@NonNull Long id, ProductRequest request);

    Mono<Void> deleteProduct(@NonNull Long id);
}
