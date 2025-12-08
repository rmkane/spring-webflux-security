package org.acme.api.service.impl;

import java.time.LocalDateTime;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.acme.api.dto.ProductRequest;
import org.acme.api.dto.ProductResponse;
import org.acme.api.mapper.ProductMapper;
import org.acme.api.service.ProductService;
import org.acme.persistence.model.Product;
import org.acme.persistence.repository.ProductRepository;

/**
 * Product service implementation
 */
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Override
    public Flux<ProductResponse> getAllProducts() {
        return productRepository.findAll()
                .map(productMapper::toResponse);
    }

    @Override
    public Mono<ProductResponse> getProductById(@NonNull Long id) {
        return productRepository.findById(id)
                .map(productMapper::toResponse);
    }

    @Override
    public Mono<ProductResponse> createProduct(ProductRequest request) {
        Product product = productMapper.toEntity(request);
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());
        return productRepository.save(product)
                .map(productMapper::toResponse);
    }

    @Override
    public Mono<ProductResponse> updateProduct(@NonNull Long id, ProductRequest request) {
        return productRepository.findById(id)
                .flatMap(existingProduct -> {
                    productMapper.updateEntityFromRequest(request, existingProduct);
                    existingProduct.setUpdatedAt(LocalDateTime.now());
                    return productRepository.save(existingProduct);
                })
                .map(productMapper::toResponse);
    }

    @Override
    public Mono<Void> deleteProduct(@NonNull Long id) {
        return productRepository.deleteById(id);
    }
}
