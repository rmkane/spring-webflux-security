package org.acme.persistence.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.r2dbc.core.DatabaseClient;

import reactor.test.StepVerifier;

import org.acme.persistence.config.TestR2dbcConfig;
import org.acme.persistence.model.Product;

@DataR2dbcTest(properties = {
        "spring.r2dbc.url=r2dbc:h2:mem:///testdb?options=DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
        "spring.r2dbc.username=sa",
        "spring.r2dbc.password="
})
@org.springframework.test.context.ContextConfiguration(classes = TestR2dbcConfig.class)
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private DatabaseClient databaseClient;

    @BeforeEach
    void setUp() {
        // Create products table for H2
        databaseClient.sql("CREATE TABLE IF NOT EXISTS products (" +
                "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                "name VARCHAR(255) NOT NULL, " +
                "description VARCHAR(255), " +
                "price DECIMAL(19,2), " +
                "stock INTEGER, " +
                "created_at TIMESTAMP, " +
                "updated_at TIMESTAMP" +
                ")")
                .fetch()
                .rowsUpdated()
                .block();

        // Clear existing data
        databaseClient.sql("DELETE FROM products").fetch().rowsUpdated().block();
    }

    @Test
    void save_ShouldSaveProduct() {
        LocalDateTime now = LocalDateTime.now();
        Product product = new Product(null, "Test Product", "Test Description", BigDecimal.valueOf(10.99), 100, now,
                now);

        StepVerifier.create(productRepository.save(product))
                .assertNext(savedProduct -> {
                    assertNotNull(savedProduct.getId());
                    assertEquals("Test Product", savedProduct.getName());
                    assertEquals("Test Description", savedProduct.getDescription());
                    assertEquals(BigDecimal.valueOf(10.99), savedProduct.getPrice());
                    assertEquals(100, savedProduct.getStock());
                })
                .verifyComplete();
    }

    @Test
    void findById_ShouldReturnProduct() {
        LocalDateTime now = LocalDateTime.now();
        Product product = new Product(null, "Test Product", "Test Description", BigDecimal.valueOf(10.99), 100, now,
                now);
        Product savedProduct = productRepository.save(product).block();

        StepVerifier.create(productRepository.findById(savedProduct.getId()))
                .assertNext(foundProduct -> {
                    assertEquals(savedProduct.getId(), foundProduct.getId());
                    assertEquals("Test Product", foundProduct.getName());
                    assertEquals(BigDecimal.valueOf(10.99), foundProduct.getPrice());
                })
                .verifyComplete();
    }

    @Test
    void findAll_ShouldReturnAllProducts() {
        LocalDateTime now = LocalDateTime.now();
        Product product1 = new Product(null, "Product 1", "Description 1", BigDecimal.valueOf(10.99), 100, now, now);
        Product product2 = new Product(null, "Product 2", "Description 2", BigDecimal.valueOf(20.99), 200, now, now);

        productRepository.save(product1).block();
        productRepository.save(product2).block();

        StepVerifier.create(productRepository.findAll())
                .assertNext(p -> assertTrue(p.getName().equals("Product 1") || p.getName().equals("Product 2")))
                .assertNext(p -> assertTrue(p.getName().equals("Product 1") || p.getName().equals("Product 2")))
                .verifyComplete();
    }

    @Test
    void findById_WhenProductNotFound_ShouldReturnEmpty() {
        StepVerifier.create(productRepository.findById(999L))
                .verifyComplete();
    }

    @Test
    void deleteById_ShouldDeleteProduct() {
        LocalDateTime now = LocalDateTime.now();
        Product product = new Product(null, "Test Product", "Test Description", BigDecimal.valueOf(10.99), 100, now,
                now);
        Product savedProduct = productRepository.save(product).block();

        StepVerifier.create(productRepository.deleteById(savedProduct.getId()))
                .verifyComplete();

        StepVerifier.create(productRepository.findById(savedProduct.getId()))
                .verifyComplete();
    }
}
