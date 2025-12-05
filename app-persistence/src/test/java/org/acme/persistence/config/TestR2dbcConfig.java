package org.acme.persistence.config;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

@SpringBootApplication
@EnableR2dbcRepositories(basePackages = "org.acme.persistence.repository")
public class TestR2dbcConfig {
}
