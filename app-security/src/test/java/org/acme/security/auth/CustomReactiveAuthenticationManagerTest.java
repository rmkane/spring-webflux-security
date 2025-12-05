package org.acme.security.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import reactor.test.StepVerifier;

class CustomReactiveAuthenticationManagerTest {

    private CustomReactiveAuthenticationManager authenticationManager;

    @BeforeEach
    void setUp() {
        authenticationManager = new CustomReactiveAuthenticationManager();
    }

    @Test
    void authenticate_WithValidCredentials_ShouldReturnAuthenticatedToken() {
        Authentication authentication = new UsernamePasswordAuthenticationToken("user", "password");

        StepVerifier.create(authenticationManager.authenticate(authentication))
                .assertNext(auth -> {
                    assertNotNull(auth);
                    assertEquals("user", auth.getName());
                    assertEquals("password", auth.getCredentials().toString());
                    assertTrue(auth.getAuthorities().stream()
                            .anyMatch(a -> a.getAuthority().equals("ROLE_USER")));
                })
                .verifyComplete();
    }

    @Test
    void authenticate_WithInvalidUsername_ShouldReturnEmpty() {
        Authentication authentication = new UsernamePasswordAuthenticationToken("invalid", "password");

        StepVerifier.create(authenticationManager.authenticate(authentication))
                .verifyComplete();
    }

    @Test
    void authenticate_WithInvalidPassword_ShouldReturnEmpty() {
        Authentication authentication = new UsernamePasswordAuthenticationToken("user", "wrongpassword");

        StepVerifier.create(authenticationManager.authenticate(authentication))
                .verifyComplete();
    }

    @Test
    void authenticate_WithBothInvalid_ShouldReturnEmpty() {
        Authentication authentication = new UsernamePasswordAuthenticationToken("invalid", "wrongpassword");

        StepVerifier.create(authenticationManager.authenticate(authentication))
                .verifyComplete();
    }

    @Test
    void authenticate_WithValidCredentials_ShouldHaveRoleUser() {
        Authentication authentication = new UsernamePasswordAuthenticationToken("user", "password");

        StepVerifier.create(authenticationManager.authenticate(authentication))
                .assertNext(auth -> {
                    assertNotNull(auth.getAuthorities());
                    assertEquals(1, auth.getAuthorities().size());
                    GrantedAuthority authority = auth.getAuthorities().iterator().next();
                    assertEquals("ROLE_USER", authority.getAuthority());
                })
                .verifyComplete();
    }

    @Test
    void authenticate_WithValidCredentials_ShouldReturnUsernamePasswordAuthenticationToken() {
        Authentication authentication = new UsernamePasswordAuthenticationToken("user", "password");

        StepVerifier.create(authenticationManager.authenticate(authentication))
                .assertNext(auth -> {
                    assertTrue(auth instanceof UsernamePasswordAuthenticationToken);
                    UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) auth;
                    assertEquals("user", token.getName());
                    assertEquals("password", token.getCredentials().toString());
                })
                .verifyComplete();
    }
}
