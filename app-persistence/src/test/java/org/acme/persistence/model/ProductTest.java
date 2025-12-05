package org.acme.persistence.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

class ProductTest {

    @Test
    void noArgsConstructor_ShouldCreateEmptyProduct() {
        Product product = new Product();
        assertNotNull(product);
    }

    @Test
    void allArgsConstructor_ShouldCreateProductWithAllFields() {
        LocalDateTime now = LocalDateTime.now();
        Product product = new Product(1L, "Test Product", "Test Description", BigDecimal.valueOf(10.99), 100, now,
                now);

        assertEquals(1L, product.getId());
        assertEquals("Test Product", product.getName());
        assertEquals("Test Description", product.getDescription());
        assertEquals(BigDecimal.valueOf(10.99), product.getPrice());
        assertEquals(100, product.getStock());
        assertEquals(now, product.getCreatedAt());
        assertEquals(now, product.getUpdatedAt());
    }

    @Test
    void setters_ShouldSetFields() {
        LocalDateTime now = LocalDateTime.now();
        Product product = new Product();
        product.setId(1L);
        product.setName("Test Product");
        product.setDescription("Test Description");
        product.setPrice(BigDecimal.valueOf(10.99));
        product.setStock(100);
        product.setCreatedAt(now);
        product.setUpdatedAt(now);

        assertEquals(1L, product.getId());
        assertEquals("Test Product", product.getName());
        assertEquals("Test Description", product.getDescription());
        assertEquals(BigDecimal.valueOf(10.99), product.getPrice());
        assertEquals(100, product.getStock());
        assertEquals(now, product.getCreatedAt());
        assertEquals(now, product.getUpdatedAt());
    }
}
