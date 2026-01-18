# payroll-demo-jpa-jooq

Demo project showing when to use Spring Data JPA for transactional write models and when to use jOOQ for read/reporting queries.

## Requirements
- Java 21
- Maven Wrapper (`./mvnw`)
- Docker (for component tests via Playtika Testcontainers)

## Build

```bash
./mvnw clean verify
```

## Run

```bash
./mvnw -pl integrations/bootstrap spring-boot:run
```

## JPA vs jOOQ Summary

- **JPA write model:** transactional batch creation, payment additions, and execution with domain invariants.
- **jOOQ read model:** SQL-first batch listing with aggregates and paging-friendly queries.

Environment variables for local runs:
- `JDBC_URL` (default: `jdbc:postgresql://localhost:5432/payroll`)
- `DB_USERNAME` (default: `payroll`)
- `DB_PASSWORD` (default: `payroll`)

## Architecture

- `domain`: pure domain model and outbound ports (no Spring, no JPA).
- `application`: use cases and transactional boundaries using `jakarta.transaction` only.
- `integrations/adapters`: Spring components for JPA and jOOQ adapters.
- `integrations/bootstrap`: Spring Boot entrypoint and all configuration.
- `component-test`: Playtika PostgreSQL container tests and context validation.

## Module Layout & Guardrails

- DDD + hexagonal split: core (`domain`, `application`) vs adapters (`integrations/*`).
- `domain` and `application` stay framework-free (no Spring/JPA/jOOQ annotations).
- Spring configuration and wiring live in `integrations/bootstrap` only.
- Use `./mvnw` for local builds and runs.

## Migrations

Flyway migrations live in `integrations/bootstrap/src/main/resources/db/migration`.
