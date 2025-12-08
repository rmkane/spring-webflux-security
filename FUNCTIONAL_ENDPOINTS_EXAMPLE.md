# Spring WebFlux Functional Endpoints Example

This document provides an example of implementing REST endpoints using Spring WebFlux's functional routing approach (`RouterFunction` and `HandlerFunction`) as an alternative to annotated controllers.

## Overview

Spring WebFlux supports two programming models:

1. **Annotated Controllers** - Using `@RestController` and `@RequestMapping` (current approach)
2. **Functional Endpoints** - Using `RouterFunction` and `HandlerFunction` (this example)

## When to Use Functional Endpoints

Functional endpoints are better suited for:

- Dynamic routing requirements
- Programmatic control over routing logic
- Building DSLs or frameworks on top of WebFlux
- Complex nested routing scenarios
- Preference for functional programming style

For standard REST CRUD operations, annotated controllers are typically simpler and more maintainable.

## Example: Product Functional Router

### Handler Functions

```java
package org.acme.api.handler;

import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import org.acme.api.dto.ProductRequest;
import org.acme.api.dto.ProductResponse;
import org.acme.api.service.ProductService;

/**
 * Handler functions for Product operations
 */
@Component
@RequiredArgsConstructor
public class ProductHandler {

    private final ProductService productService;

    public Mono<ServerResponse> getAllProducts(ServerRequest request) {
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(productService.getAllProducts(), ProductResponse.class);
    }

    public Mono<ServerResponse> getProductById(ServerRequest request) {
        @NonNull Long id = Long.parseLong(request.pathVariable("id"));
        return productService.getProductById(id)
                .flatMap(product -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(product))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> createProduct(ServerRequest request) {
        return request.bodyToMono(ProductRequest.class)
                .flatMap(productService::createProduct)
                .flatMap(product -> ServerResponse.status(201)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(product))
                .onErrorResume(e -> ServerResponse.badRequest().build());
    }

    public Mono<ServerResponse> updateProduct(ServerRequest request) {
        @NonNull Long id = Long.parseLong(request.pathVariable("id"));
        return request.bodyToMono(ProductRequest.class)
                .flatMap(productRequest -> productService.updateProduct(id, productRequest))
                .flatMap(product -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(product))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> deleteProduct(ServerRequest request) {
        @NonNull Long id = Long.parseLong(request.pathVariable("id"));
        return productService.deleteProduct(id)
                .then(ServerResponse.noContent().build())
                .switchIfEmpty(ServerResponse.notFound().build());
    }
}
```

### Router Configuration

```java
package org.acme.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import org.acme.api.handler.ProductHandler;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;

/**
 * Functional routing configuration for Product endpoints
 */
@Configuration
public class ProductRouterConfig {

    @Bean
    public RouterFunction<ServerResponse> productRoutes(ProductHandler productHandler) {
        return RouterFunctions.route()
                .GET("/api/products", accept(MediaType.APPLICATION_JSON), productHandler::getAllProducts)
                .GET("/api/products/{id}", accept(MediaType.APPLICATION_JSON), productHandler::getProductById)
                .POST("/api/products", contentType(MediaType.APPLICATION_JSON), productHandler::createProduct)
                .PUT("/api/products/{id}", contentType(MediaType.APPLICATION_JSON), productHandler::updateProduct)
                .DELETE("/api/products/{id}", productHandler::deleteProduct)
                .build();
    }
}
```

## Comparison: Annotated vs Functional

### Annotated Controller (Current Approach)

```java
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {
    
    private final ProductService productService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Flux<ProductResponse> getAllProducts() {
        return productService.getAllProducts();
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ProductResponse> getProductById(@PathVariable("id") @NonNull Long id) {
        return productService.getProductById(id);
    }
    
    // ... other methods
}
```

### Functional Handler (Alternative Approach)

```java
@Component
@RequiredArgsConstructor
public class ProductHandler {
    
    private final ProductService productService;

    public Mono<ServerResponse> getAllProducts(ServerRequest request) {
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(productService.getAllProducts(), ProductResponse.class);
    }

    public Mono<ServerResponse> getProductById(ServerRequest request) {
        Long id = Long.parseLong(request.pathVariable("id"));
        return productService.getProductById(id)
                .flatMap(product -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(product))
                .switchIfEmpty(ServerResponse.notFound().build());
    }
}
```

## Key Differences

| Aspect | Annotated Controllers | Functional Endpoints |
|--------|----------------------|---------------------|
| **Routing** | Declarative via annotations | Programmatic via RouterFunction |
| **Request Handling** | Direct method parameters | ServerRequest object |
| **Response** | Return Mono/Flux directly | Return ServerResponse |
| **Error Handling** | @ExceptionHandler | onErrorResume/onErrorReturn |
| **Validation** | @Valid annotation | Manual validation |
| **Swagger/OpenAPI** | Automatic | Requires additional configuration |
| **Code Verbosity** | Less verbose | More verbose |
| **Flexibility** | Less flexible | More flexible |

## Advanced Features

### Nested Routes

```java
@Bean
public RouterFunction<ServerResponse> nestedRoutes(ProductHandler productHandler) {
    return RouterFunctions.route()
            .path("/api", builder -> builder
                    .path("/products", nestedBuilder -> nestedBuilder
                            .GET("", productHandler::getAllProducts)
                            .GET("/{id}", productHandler::getProductById)
                            .POST("", productHandler::createProduct)
                            .PUT("/{id}", productHandler::updateProduct)
                            .DELETE("/{id}", productHandler::deleteProduct)
                    )
            )
            .build();
}
```

### Filters

```java
@Bean
public RouterFunction<ServerResponse> routesWithFilter(ProductHandler productHandler) {
    return RouterFunctions.route()
            .GET("/api/products", productHandler::getAllProducts)
            .filter((request, next) -> {
                // Add custom filter logic
                String authHeader = request.headers().firstHeader("Authorization");
                if (authHeader == null) {
                    return ServerResponse.status(401).build();
                }
                return next.handle(request);
            })
            .build();
}
```

### Request Predicates

```java
@Bean
public RouterFunction<ServerResponse> routesWithPredicates(ProductHandler productHandler) {
    return RouterFunctions.route()
            .GET("/api/products", 
                    accept(MediaType.APPLICATION_JSON)
                    .and(queryParam("sort", "name")),
                    productHandler::getAllProducts)
            .GET("/api/products/{id}",
                    accept(MediaType.APPLICATION_JSON)
                    .and(pathVariable("id", Long::parseLong)),
                    productHandler::getProductById)
            .build();
}
```

## Testing Functional Endpoints

```java
@WebFluxTest
class ProductHandlerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private ProductService productService;

    @Test
    void getAllProducts_ShouldReturnProducts() {
        ProductResponse product = new ProductResponse(/* ... */);
        when(productService.getAllProducts()).thenReturn(Flux.just(product));

        webTestClient.get()
                .uri("/api/products")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(ProductResponse.class)
                .hasSize(1);
    }
}
```

## Swagger/OpenAPI Integration

To enable Swagger with functional endpoints, you need to configure it explicitly:

```java
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("WebFlux API")
                        .version("1.0.0"));
    }
}
```

Note: Swagger UI integration with functional endpoints requires additional configuration compared to annotated controllers.

## Migration Strategy

If you want to migrate from annotated controllers to functional endpoints:

1. **Create Handler classes** - Extract handler logic from controllers
2. **Create Router configurations** - Define routes programmatically
3. **Update tests** - Adjust test setup for functional endpoints
4. **Configure Swagger** - Add explicit OpenAPI configuration
5. **Remove controllers** - Delete old controller classes

## Recommendation

For this project, **keep using annotated controllers** because:

- ✅ Simpler and more maintainable for standard REST APIs
- ✅ Better Swagger/OpenAPI integration
- ✅ Less boilerplate code
- ✅ Easier to test with existing test infrastructure
- ✅ More familiar to most Spring developers

Use functional endpoints when you need:

- Dynamic routing based on runtime conditions
- Complex nested routing scenarios
- Building a framework or DSL on top of WebFlux
- Preference for functional programming style

## References

- [Spring WebFlux Functional Endpoints Documentation](https://docs.spring.io/spring-framework/reference/web/webflux.html#webflux-fn)
- [RouterFunction API](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/reactive/function/server/RouterFunction.html)
- [HandlerFunction API](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/reactive/function/server/HandlerFunction.html)
