package org.acme.api.service.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
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

import org.acme.api.dto.ProductRequest;
import org.acme.api.dto.ProductResponse;
import org.acme.api.mapper.ProductMapper;
import org.acme.api.service.ProductService;
import org.acme.persistence.model.Product;
import org.acme.persistence.repository.ProductRepository;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductServiceImpl productServiceImpl;

    private ProductService productService;

    private Product product;
    private ProductResponse productResponse;
    private ProductRequest productRequest;

    @BeforeEach
    void setUp() {
        LocalDateTime now = LocalDateTime.now();
        product = new Product(1L, "Product 1", "Description 1", BigDecimal.valueOf(10.99), 100, now, now);
        productResponse = new ProductResponse(1L, "Product 1", "Description 1", BigDecimal.valueOf(10.99), 100, now,
                now);
        productRequest = new ProductRequest("Product 1", "Description 1", BigDecimal.valueOf(10.99), 100);
        productService = productServiceImpl;
    }

    @Test
    void getAllProducts_ShouldReturnAllProducts() {
        when(productRepository.findAll()).thenReturn(Flux.just(product));
        when(productMapper.toResponse(product)).thenReturn(productResponse);

        StepVerifier.create(productService.getAllProducts())
                .expectNext(productResponse)
                .verifyComplete();

        verify(productRepository).findAll();
        verify(productMapper).toResponse(product);
    }

    @Test
    void getProductById_ShouldReturnProduct() {
        Long productId = 1L;
        when(productRepository.findById(productId)).thenReturn(Mono.just(product));
        when(productMapper.toResponse(product)).thenReturn(productResponse);

        StepVerifier.create(productService.getProductById(productId))
                .expectNext(productResponse)
                .verifyComplete();

        verify(productRepository).findById(productId);
        verify(productMapper).toResponse(product);
    }

    @Test
    @SuppressWarnings("null")
    void createProduct_ShouldReturnCreatedProduct() {
        when(productMapper.toEntity(productRequest)).thenReturn(product);
        when(productRepository.save(any(Product.class))).thenReturn(Mono.just(product));
        when(productMapper.toResponse(product)).thenReturn(productResponse);

        StepVerifier.create(productService.createProduct(productRequest))
                .expectNext(productResponse)
                .verifyComplete();

        verify(productMapper).toEntity(productRequest);
        verify(productRepository).save(any(Product.class));
        verify(productMapper).toResponse(product);
    }

    @Test
    @SuppressWarnings("null")
    void updateProduct_ShouldReturnUpdatedProduct() {
        Long productId = 1L;
        when(productRepository.findById(productId)).thenReturn(Mono.just(product));
        when(productRepository.save(any(Product.class))).thenReturn(Mono.just(product));
        when(productMapper.toResponse(product)).thenReturn(productResponse);

        StepVerifier.create(productService.updateProduct(productId, productRequest))
                .expectNext(productResponse)
                .verifyComplete();

        verify(productRepository).findById(productId);
        verify(productMapper).updateEntityFromRequest(productRequest, product);
        verify(productRepository).save(any(Product.class));
        verify(productMapper).toResponse(product);
    }

    @Test
    void deleteProduct_ShouldDeleteProduct() {
        Long productId = 1L;
        when(productRepository.deleteById(productId)).thenReturn(Mono.empty());

        StepVerifier.create(productService.deleteProduct(productId))
                .verifyComplete();

        verify(productRepository).deleteById(productId);
    }
}
