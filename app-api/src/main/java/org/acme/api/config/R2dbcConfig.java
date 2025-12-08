package org.acme.api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.lang.NonNull;

import io.r2dbc.postgresql.PostgresqlConnectionConfiguration;
import io.r2dbc.postgresql.PostgresqlConnectionFactory;
import io.r2dbc.spi.ConnectionFactory;

/**
 * R2DBC configuration for reactive database access
 */
@Configuration
@EnableR2dbcRepositories(basePackages = "org.acme.persistence.repository")
public class R2dbcConfig extends AbstractR2dbcConfiguration {

    private static final String R2DBC_POSTGRESQL_PREFIX = "r2dbc:postgresql://";
    private static final int DEFAULT_POSTGRES_PORT = 5432;
    private static final String DEFAULT_DATABASE_NAME = "webflux_db";

    @Value("${spring.r2dbc.url}")
    private String r2dbcUrl;

    @Value("${spring.r2dbc.username}")
    private String username;

    @Value("${spring.r2dbc.password}")
    private String password;

    @Override
    @Bean
    @NonNull
    public ConnectionFactory connectionFactory() {
        // Parse the R2DBC URL: r2dbc:postgresql://localhost:5432/webflux_db
        String url = r2dbcUrl.replace(R2DBC_POSTGRESQL_PREFIX, "");
        String[] parts = url.split("/");
        String[] hostPort = parts[0].split(":");
        String host = hostPort[0];
        int port = hostPort.length > 1 ? Integer.parseInt(hostPort[1]) : DEFAULT_POSTGRES_PORT;
        String database = parts.length > 1 ? parts[1] : DEFAULT_DATABASE_NAME;

        return new PostgresqlConnectionFactory(
                PostgresqlConnectionConfiguration.builder()
                        .host(host)
                        .port(port)
                        .database(database)
                        .username(username)
                        .password(password)
                        .build());
    }
}
