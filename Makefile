# Default target
.DEFAULT_GOAL := help

.PHONY: help \
		build clean full-clean \
		start stop restart \
		db-up db-down db-logs db-reset db-shell \
		test coverage coverage-check coverage-summary \
		format lint


# Variables
APP_MODULE := app-api
APP_JAR := $(APP_MODULE)/target/$(APP_MODULE)-1.0.0-SNAPSHOT.jar

help: ## Show this help message
	@echo "Available targets:"
	@grep -E '^[a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST) | awk 'BEGIN {FS = ":.*?## "}; {printf "  \033[36m%-15s\033[0m %s\n", $$1, $$2}'

# Build targets
build: ## Build the project
	@echo "Building project..."
	mvn clean install -DskipTests

clean: ## Clean build artifacts
	@echo "Cleaning project..."
	mvn clean

full-clean: clean ## Clean everything including Docker volumes
	docker compose down -v
	@echo "Full clean complete"

# Application targets
start: build db-up ## Build and start the application
	@echo "Starting application..."
	@if [ ! -f $(APP_JAR) ]; then \
		echo "JAR file not found. Building project..."; \
		mvn clean install -DskipTests; \
	fi
	cd $(APP_MODULE) && mvn spring-boot:run

stop: ## Stop the application (if running in background)
	@pkill -f "spring-boot:run" || echo "Application not running"

restart: stop start ## Restart the application

# Database targets
db-up: ## Start PostgreSQL database
	@echo "Starting PostgreSQL database..."
	docker compose up -d postgres
	@echo "Waiting for database to be ready..."
	@sleep 5
	@docker compose ps

db-down: ## Stop PostgreSQL database
	@echo "Stopping PostgreSQL database..."
	docker compose down

db-logs: ## Show database logs
	docker compose logs -f postgres

db-reset: db-down db-up ## Reset database (stop and start)
	@echo "Database reset complete"

db-shell: ## Open PostgreSQL shell
	@echo "Connecting to PostgreSQL database..."
	@docker compose exec postgres psql -U webflux_user -d webflux_db

# Test targets
test: ## Run tests
	mvn test

coverage: ## Run tests and generate coverage report
	@echo "Running tests with coverage..."
	mvn clean test jacoco:report -rf app-parent
	@echo "Coverage report generated at app-api/target/site/jacoco/index.html"

coverage-check: ## Run tests and check coverage threshold
	@echo "Running tests and checking coverage..."
	mvn clean test jacoco:check -rf app-parent
	@echo "Coverage check passed"

coverage-summary: ## Display aggregated coverage summary in console
	@./coverage-summary.sh

# Code quality targets
format: ## Format code using Spotless
	@echo "Formatting code with Spotless..."
	mvn com.diffplug.spotless:spotless-maven-plugin:apply -rf app-parent
	@echo "Formatting complete"

lint: ## Check code formatting with Spotless
	@echo "Checking code formatting..."
	mvn com.diffplug.spotless:spotless-maven-plugin:check -rf app-parent || (echo "Formatting violations found. Run 'make format' to fix them." && exit 1)
	@echo "Code formatting check passed"

