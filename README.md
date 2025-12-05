# Spring WebFlux Security Multi-Module Project

A multi-module Spring Boot project demonstrating Spring WebFlux with Spring Security integration, reactive database access with R2DBC, and comprehensive testing with code coverage.

## Project Structure

```none
spring-webflux-security/
├── app-parent/           # Parent module with common dependencies
├── app-security/         # Security library module
├── app-persistence/      # Persistence module with entities and repositories
└── app-api/              # WebFlux application module
```

## Modules

### Root POM

The root `pom.xml` defines the multi-module structure and common properties.

### App Parent

Contains common dependencies and dependency management for:

- Spring Boot 3.5.6
- Spring Security (version managed by Spring Boot)
- Spring WebFlux
- Spring Data R2DBC
- Lombok
- MapStruct 1.6.3
- SpringDoc OpenAPI 2.8.14
- JaCoCo 0.8.14
- Spotless 2.43.0
- Testing dependencies

### App Security

Provides Spring Security configuration and authentication components:

- `SecurityConfig`: Reactive security configuration with HTTP Basic authentication
- `CustomReactiveAuthenticationManager`: Custom authentication manager

### App Persistence

Contains database entities, repositories, and Flyway migrations:

- **Models**: User and Product entities with R2DBC annotations
- **Repositories**: Reactive Spring Data R2DBC repositories
- **Migrations**: Flyway database migration scripts

### App API

The main application module containing:

- **Configuration**: R2DBC configuration for reactive database access
- **DTOs**: Data Transfer Objects for request/response
- **Mappers**: MapStruct mappers for DTO-entity conversion
- **Services**: Reactive service layer for business logic
- **Controllers**: REST endpoints for Users and Products
- **Public Controller**: Unauthenticated endpoints (health, info)

## Prerequisites

- Java 17 or higher
- Maven 3.x
- Docker and Docker Compose (for database)

## Quick Start

### 1. Start the Database

```bash
make db-up
```

Or manually:

```bash
docker-compose up -d postgres
```

### 2. Build the Project

```bash
make build
```

Or manually:

```bash
mvn clean install
```

### 3. Run the Application

```bash
make start
```

Or manually:

```bash
cd app-api
mvn spring-boot:run
```

The application will start on port 8080.

## Makefile Targets

The project includes a Makefile for common tasks:

### Build Targets

- `make build` - Build the project (skip tests)
- `make clean` - Clean build artifacts
- `make full-clean` - Clean everything including Docker volumes

### Application Targets

- `make start` - Build and start the application
- `make stop` - Stop the application
- `make restart` - Restart the application

### Database Targets

- `make db-up` - Start PostgreSQL database
- `make db-down` - Stop PostgreSQL database
- `make db-logs` - Show database logs
- `make db-reset` - Reset database (stop and start)

### Test Targets

- `make test` - Run all tests
- `make coverage` - Run tests and generate coverage report
- `make coverage-check` - Run tests and check coverage threshold (50% minimum)

### Code Quality Targets

- `make format` - Format code using Spotless
- `make lint` - Check code formatting with Spotless

## API Endpoints

### Public Endpoints (No Authentication Required)

- `GET /api/public/health` - Health check
- `GET /api/public/info` - Application information

### Protected Endpoints (Authentication Required)

#### Users

- `GET /api/users` - Get all users
- `GET /api/users/{id}` - Get user by ID
- `POST /api/users` - Create a new user
- `PUT /api/users/{id}` - Update user
- `DELETE /api/users/{id}` - Delete user

#### Products

- `GET /api/products` - Get all products
- `GET /api/products/{id}` - Get product by ID
- `POST /api/products` - Create a new product
- `PUT /api/products/{id}` - Update product
- `DELETE /api/products/{id}` - Delete product

### API Documentation

Swagger/OpenAPI documentation is available at:

- `http://localhost:8080/swagger-ui.html`

## Security

The application uses HTTP Basic Authentication. Default credentials:

- Username: `user`
- Password: `password`

Public endpoints (`/api/public/**`) are accessible without authentication.

## Database

The project uses PostgreSQL 17 with R2DBC for reactive database access. Database migrations are managed by Flyway.

### Database Configuration

- **Host**: localhost
- **Port**: 5432
- **Database**: webflux_db
- **Username**: webflux_user
- **Password**: webflux_password

### Running Migrations

Migrations run automatically on application startup via Flyway.

## Testing

The project includes comprehensive test coverage:

### Test Statistics

- **Total Tests**: 55+ tests across all modules
- **App API**: 26 tests (controllers, services, configuration)
- **App Persistence**: 19 tests (repositories, models)
- **App Security**: 10 tests (authentication, configuration)

### Running Tests

```bash
# Run all tests
make test

# Run tests for a specific module
mvn test -pl app-api
mvn test -pl app-persistence
mvn test -pl app-security

# Generate coverage report
make coverage

# Check coverage threshold
make coverage-check
```

### Coverage Reports

Coverage reports are generated in each module's `target/site/jacoco/index.html`:

- `app-api/target/site/jacoco/index.html`
- `app-persistence/target/site/jacoco/index.html`
- `app-security/target/site/jacoco/index.html`

Coverage threshold: 50% minimum line coverage (excluding mappers and main application class).

## Code Quality

### Code Formatting

The project uses Spotless with Eclipse formatter:

```bash
# Format code
make format

# Check formatting
make lint
```

### Import Order

Imports are ordered as: `java`, `javax`, `jakarta`, `org`, `com`, `io`, `lombok`, `reactor`, `org.acme`

## Technologies

- **Java**: 17
- **Spring Boot**: 3.5.6
- **Spring WebFlux**: Reactive web framework
- **Spring Security**: Reactive security (version managed by Spring Boot)
- **Spring Data R2DBC**: Reactive database access
- **PostgreSQL**: 17 (via Docker)
- **Flyway**: Database migrations
- **Lombok**: Reducing boilerplate code
- **MapStruct**: DTO-entity mapping
- **SpringDoc OpenAPI**: API documentation
- **JaCoCo**: Code coverage
- **Spotless**: Code formatting
- **Maven**: Build tool
- **Docker Compose**: Database containerization

## Project Organization

### Package Structure

- `org.acme.api.*` - API module (controllers, services, DTOs, mappers, config)
- `org.acme.persistence.*` - Persistence module (models, repositories)
- `org.acme.security.*` - Security module (config, authentication)

### Architecture

- **Controllers**: Thin layer, delegates to services
- **Services**: Business logic and DTO-entity mapping
- **Repositories**: Reactive data access
- **DTOs**: Data Transfer Objects for API boundaries
- **Mappers**: MapStruct for automatic DTO-entity conversion

## License

This project is for demonstration purposes.
