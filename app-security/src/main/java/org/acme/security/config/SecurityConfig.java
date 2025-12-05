package org.acme.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

import org.acme.security.auth.CustomReactiveAuthenticationManager;

/**
 * Security configuration for reactive Spring Security
 */
@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public CustomReactiveAuthenticationManager authenticationManager() {
        return new CustomReactiveAuthenticationManager();
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers("/api/public/**").permitAll()
                        .anyExchange().authenticated())
                .httpBasic(httpBasic -> httpBasic
                        .authenticationManager(authenticationManager()))
                .csrf(csrf -> csrf.disable())
                .build();
    }
}
