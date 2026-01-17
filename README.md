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

## Migrations

Flyway migrations live in `integrations/bootstrap/src/main/resources/db/migration`.
