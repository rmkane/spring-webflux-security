# Spring WebFlux Security Multi-Module Project

A multi-module Spring Boot project demonstrating Spring WebFlux with Spring Security integration.

## Project Structure

```none
spring-webflux-security/
├── pom.xml                    # Root POM
├── parent/                    # Parent module with common dependencies
│   └── pom.xml
├── security-library/          # Security library module
│   ├── pom.xml
│   └── src/main/java/com/example/security/
│       ├── config/
│       │   └── SecurityConfig.java
│       └── auth/
│           └── CustomReactiveAuthenticationManager.java
└── webflux-app/               # WebFlux application module
    ├── pom.xml
    └── src/main/java/com/example/webflux/
        ├── WebfluxApplication.java
        ├── controller/
        │   ├── UserController.java
        │   ├── ProductController.java
        │   └── PublicController.java
        ├── service/
        │   ├── UserService.java
        │   └── ProductService.java
        └── model/
            ├── User.java
            └── Product.java
```

## Modules

### Root POM

The root `pom.xml` defines the multi-module structure and common properties.

### Parent Module

Contains common dependencies and dependency management for:

- Spring Boot 3.5.6
- Spring Security (version managed by Spring Boot)
- Spring WebFlux
- Lombok
- Testing dependencies

### Security Library

Provides Spring Security configuration and authentication components:

- `SecurityConfig`: Reactive security configuration
- `CustomReactiveAuthenticationManager`: Custom authentication manager

### WebFlux App

The main application module containing:

- **Models**: User and Product entities
- **Services**: Reactive service layer for business logic
- **Controllers**: REST endpoints for Users and Products
- **Public Controller**: Unauthenticated endpoints (health, info)

## Building the Project

```bash
mvn clean install
```

## Running the Application

```bash
cd webflux-app
mvn spring-boot:run
```

The application will start on port 8080.

## API Endpoints

### Public Endpoints (No Authentication Required)

- `GET /api/public/health` - Health check
- `GET /api/public/info` - Application information

### Protected Endpoints (Authentication Required)

- `GET /api/users` - Get all users
- `GET /api/users/{id}` - Get user by ID
- `POST /api/users` - Create a new user
- `PUT /api/users/{id}` - Update user
- `DELETE /api/users/{id}` - Delete user

- `GET /api/products` - Get all products
- `GET /api/products/{id}` - Get product by ID
- `POST /api/products` - Create a new product
- `PUT /api/products/{id}` - Update product
- `DELETE /api/products/{id}` - Delete product

## Security

The application uses HTTP Basic Authentication. Default credentials:

- Username: `user`
- Password: `password`

## Technologies

- Java 17
- Spring Boot 3.5.6
- Spring WebFlux (Reactive)
- Spring Security (version managed by Spring Boot)
- Maven 3.x
- Lombok
