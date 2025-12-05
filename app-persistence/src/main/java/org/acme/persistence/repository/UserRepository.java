package org.acme.persistence.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import reactor.core.publisher.Mono;

import org.acme.persistence.model.User;

/**
 * Reactive repository for User entities
 */
@Repository
public interface UserRepository extends ReactiveCrudRepository<User, Long> {
    Mono<User> findByUsername(String username);

    Mono<User> findByEmail(String email);
}
