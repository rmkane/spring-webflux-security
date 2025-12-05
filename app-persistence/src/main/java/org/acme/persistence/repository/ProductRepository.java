package org.acme.persistence.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import org.acme.persistence.model.Product;

/**
 * Reactive repository for Product entities
 */
@Repository
public interface ProductRepository extends ReactiveCrudRepository<Product, Long> {
}
