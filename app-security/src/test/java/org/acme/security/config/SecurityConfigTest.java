package org.acme.security.config;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.test.context.TestPropertySource;

import org.acme.security.auth.CustomReactiveAuthenticationManager;

@SpringBootTest(classes = SecurityConfig.class)
@TestPropertySource(properties = {
        "spring.main.web-application-type=reactive"
})
class SecurityConfigTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void authenticationManager_ShouldBeConfigured() {
        CustomReactiveAuthenticationManager manager = applicationContext.getBean(
                CustomReactiveAuthenticationManager.class);
        assertNotNull(manager);
    }

    @Test
    void authenticationManager_ShouldBeReactiveAuthenticationManager() {
        ReactiveAuthenticationManager manager = applicationContext.getBean(
                ReactiveAuthenticationManager.class);
        assertNotNull(manager);
        assertTrue(manager instanceof CustomReactiveAuthenticationManager);
    }

    @Test
    void securityWebFilterChain_ShouldBeConfigured() {
        SecurityWebFilterChain filterChain = applicationContext.getBean(SecurityWebFilterChain.class);
        assertNotNull(filterChain);
    }

    @Test
    void securityConfig_ShouldCreateAllBeans() {
        assertNotNull(applicationContext.getBean(SecurityConfig.class));
        assertNotNull(applicationContext.getBean(CustomReactiveAuthenticationManager.class));
        assertNotNull(applicationContext.getBean(SecurityWebFilterChain.class));
    }
}
