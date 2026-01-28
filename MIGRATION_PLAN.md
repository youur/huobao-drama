# Backend Migration Plan: Go to Java (Spring Boot 2.7, JDK 8)

This document outlines the phased migration of the Huobao Drama backend from Go to Java.

## Phase A: Project Skeleton & Configuration
**Goal**: A bootable Java application with health checks and basic configuration.

### A1. Project Skeleton
- **Framework**: Spring Boot 2.7.x
- **JDK**: 8
- **Build Tool**: Maven
- **Package Structure**: `com.huobao.drama`
    - `.api`: Controllers and DTOs
    - `.application`: Service layer (Business Logic)
    - `.domain`: Entities and Repository interfaces
    - `.infrastructure`: Database config, external API clients (Feign/OkHttp), storage implementations
    - `.config`: General Spring configuration (CORS, Security, etc.)
    - `.common`: Utilities, constants, and shared components

### A2. Dependencies (`pom.xml`)
- `spring-boot-starter-web`: RESTful APIs
- `spring-boot-starter-validation`: DTO validation
- `spring-boot-starter-data-jpa`: Database ORM
- `spring-boot-starter-actuator`: Health checks and monitoring
- `flyway-core`: Database migrations
- `lombok`: Boilerplate reduction
- `bucket4j-core`: Rate limiting
- `spring-cloud-starter-openfeign`: External AI/Video service clients
- `sqlite-jdbc`: Database driver (matching existing SQLite setup)

### A3. Configuration (`application.yml`)
- Mapping existing `config.example.yaml` keys to Spring properties.
- Environment variable support for sensitive data (API keys).
- CORS and Rate Limiting configurations.

### A4. Infrastructure & Operations
- `Actuator` for health checks.
- `Logback` for logging with `MDC` (traceId) for request tracking.
- `Dockerfile` and `docker-compose.yml` (Java version).

---

## Phase B: Database & Entities
**Goal**: Functional persistence layer with JPA and Flyway.

### B1. Flyway Migration
- Migrate `init.sql` and subsequent migrations to `src/main/resources/db/migration`.
- Ensure naming convention: `VYYYYMMDDHHMM__description.sql`.

### B2. JPA Entities
- Map all tables from `init.sql` to Java `@Entity` classes.
- Use `Lombok` for getters/setters.
- Implement `JpaRepository` for each entity.

### B3. Standards & Conventions
- **Pagination**: 1-based indexing, max size 200.
- **Time**: All timestamps stored and transmitted in UTC.
- **JSON Fields**: Map SQLite TEXT/JSON columns to Java objects/maps using custom AttributeConverters if necessary.

---

## Phase C: Business Logic & API (Upcoming)
**Goal**: Feature parity with the Go implementation.

1. **AI Integration**: Implement OpenAI, Gemini, and Volcengine clients.
2. **Drama Management**: CRUD for Dramas, Episodes, Scenes, etc.
3. **Task System**: Background processing for image/video generation.
4. **Storage**: Local storage implementation (matching Go's `storage` package).
