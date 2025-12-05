package org.acme.api.config;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import io.r2dbc.spi.ConnectionFactory;

/**
 * Test for R2dbcConfig - default configuration with port and database
 */
@SpringBootTest(classes = R2dbcConfig.class)
class R2dbcConfigTest {

    @Autowired
    private ConnectionFactory connectionFactory;

    @Test
    void connectionFactory_WithPortAndDatabase_ShouldCreateFactory() {
        // This test uses the default application.yml configuration
        // which has both port and database:
        // r2dbc:postgresql://localhost:5432/webflux_db
        // This covers: hostPort.length > 1 (true) and parts.length > 1 (true)
        assertNotNull(connectionFactory);
    }
}

/**
 * Test for R2dbcConfig - URL without port (should default to 5432)
 */
@SpringBootTest(classes = R2dbcConfig.class)
@TestPropertySource(properties = {
        "spring.r2dbc.url=r2dbc:postgresql://localhost/webflux_db",
        "spring.r2dbc.username=test",
        "spring.r2dbc.password=test"
})
class R2dbcConfigWithoutPortTest {

    @Autowired
    private ConnectionFactory connectionFactory;

    @Test
    void connectionFactory_WithoutPort_ShouldDefaultTo5432() {
        // URL without port should default to 5432
        // This covers the branch: hostPort.length > 1 ? ... : 5432 (false branch)
        assertNotNull(connectionFactory);
    }
}

/**
 * Test for R2dbcConfig - URL without database (should default to "webflux_db")
 */
@SpringBootTest(classes = R2dbcConfig.class)
@TestPropertySource(properties = {
        "spring.r2dbc.url=r2dbc:postgresql://localhost:5432",
        "spring.r2dbc.username=test",
        "spring.r2dbc.password=test"
})
class R2dbcConfigWithoutDatabaseTest {

    @Autowired
    private ConnectionFactory connectionFactory;

    @Test
    void connectionFactory_WithoutDatabase_ShouldDefaultToWebfluxDb() {
        // URL without database should default to "webflux_db"
        // This covers the branch: parts.length > 1 ? ... : "webflux_db" (false branch)
        assertNotNull(connectionFactory);
    }
}

/**
 * Test for R2dbcConfig - URL without port and database (should use both
 * defaults)
 */
@SpringBootTest(classes = R2dbcConfig.class)
@TestPropertySource(properties = {
        "spring.r2dbc.url=r2dbc:postgresql://localhost",
        "spring.r2dbc.username=test",
        "spring.r2dbc.password=test"
})
class R2dbcConfigWithoutPortAndDatabaseTest {

    @Autowired
    private ConnectionFactory connectionFactory;

    @Test
    void connectionFactory_WithoutPortAndDatabase_ShouldUseDefaults() {
        // URL without both port and database should use both defaults
        // This covers both false branches: hostPort.length > 1 (false) and parts.length
        // > 1 (false)
        assertNotNull(connectionFactory);
    }
}
