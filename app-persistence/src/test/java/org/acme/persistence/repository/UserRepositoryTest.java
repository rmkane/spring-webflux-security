package org.acme.persistence.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.r2dbc.core.DatabaseClient;

import reactor.test.StepVerifier;

import org.acme.persistence.config.TestR2dbcConfig;
import org.acme.persistence.model.User;

@DataR2dbcTest(properties = {
        "spring.r2dbc.url=r2dbc:h2:mem:///testdb?options=DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
        "spring.r2dbc.username=sa",
        "spring.r2dbc.password="
})
@org.springframework.test.context.ContextConfiguration(classes = TestR2dbcConfig.class)
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DatabaseClient databaseClient;

    @BeforeEach
    void setUp() {
        // Create users table for H2
        databaseClient.sql("CREATE TABLE IF NOT EXISTS users (" +
                "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                "username VARCHAR(255) NOT NULL, " +
                "email VARCHAR(255) NOT NULL, " +
                "first_name VARCHAR(255), " +
                "last_name VARCHAR(255), " +
                "created_at TIMESTAMP, " +
                "updated_at TIMESTAMP" +
                ")")
                .fetch()
                .rowsUpdated()
                .block();

        // Clear existing data
        databaseClient.sql("DELETE FROM users").fetch().rowsUpdated().block();
    }

    @Test
    void save_ShouldSaveUser() {
        LocalDateTime now = LocalDateTime.now();
        User user = new User(null, "testuser", "test@example.com", "Test", "User", now, now);

        StepVerifier.create(userRepository.save(user))
                .assertNext(savedUser -> {
                    assertNotNull(savedUser.getId());
                    assertEquals("testuser", savedUser.getUsername());
                    assertEquals("test@example.com", savedUser.getEmail());
                    assertEquals("Test", savedUser.getFirstName());
                    assertEquals("User", savedUser.getLastName());
                })
                .verifyComplete();
    }

    @Test
    void findById_ShouldReturnUser() {
        LocalDateTime now = LocalDateTime.now();
        User user = new User(null, "testuser", "test@example.com", "Test", "User", now, now);
        User savedUser = userRepository.save(user).block();

        StepVerifier.create(userRepository.findById(savedUser.getId()))
                .assertNext(foundUser -> {
                    assertEquals(savedUser.getId(), foundUser.getId());
                    assertEquals("testuser", foundUser.getUsername());
                    assertEquals("test@example.com", foundUser.getEmail());
                })
                .verifyComplete();
    }

    @Test
    void findAll_ShouldReturnAllUsers() {
        LocalDateTime now = LocalDateTime.now();
        User user1 = new User(null, "user1", "user1@example.com", "John", "Doe", now, now);
        User user2 = new User(null, "user2", "user2@example.com", "Jane", "Smith", now, now);

        userRepository.save(user1).block();
        userRepository.save(user2).block();

        StepVerifier.create(userRepository.findAll())
                .assertNext(u -> assertTrue(u.getUsername().equals("user1") || u.getUsername().equals("user2")))
                .assertNext(u -> assertTrue(u.getUsername().equals("user1") || u.getUsername().equals("user2")))
                .verifyComplete();
    }

    @Test
    void findByUsername_ShouldReturnUser() {
        LocalDateTime now = LocalDateTime.now();
        User user = new User(null, "testuser", "test@example.com", "Test", "User", now, now);
        userRepository.save(user).block();

        StepVerifier.create(userRepository.findByUsername("testuser"))
                .assertNext(foundUser -> {
                    assertEquals("testuser", foundUser.getUsername());
                    assertEquals("test@example.com", foundUser.getEmail());
                })
                .verifyComplete();
    }

    @Test
    void findByUsername_WhenUserNotFound_ShouldReturnEmpty() {
        StepVerifier.create(userRepository.findByUsername("nonexistent"))
                .verifyComplete();
    }

    @Test
    void findByEmail_ShouldReturnUser() {
        LocalDateTime now = LocalDateTime.now();
        User user = new User(null, "testuser", "test@example.com", "Test", "User", now, now);
        userRepository.save(user).block();

        StepVerifier.create(userRepository.findByEmail("test@example.com"))
                .assertNext(foundUser -> {
                    assertEquals("testuser", foundUser.getUsername());
                    assertEquals("test@example.com", foundUser.getEmail());
                })
                .verifyComplete();
    }

    @Test
    void findByEmail_WhenUserNotFound_ShouldReturnEmpty() {
        StepVerifier.create(userRepository.findByEmail("nonexistent@example.com"))
                .verifyComplete();
    }

    @Test
    void deleteById_ShouldDeleteUser() {
        LocalDateTime now = LocalDateTime.now();
        User user = new User(null, "testuser", "test@example.com", "Test", "User", now, now);
        User savedUser = userRepository.save(user).block();

        StepVerifier.create(userRepository.deleteById(savedUser.getId()))
                .verifyComplete();

        StepVerifier.create(userRepository.findById(savedUser.getId()))
                .verifyComplete();
    }
}
