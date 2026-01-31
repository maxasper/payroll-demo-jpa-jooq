# Demo Script (5 minutes)

## Setup

- Start the app:

```bash
./mvnw -pl integrations/bootstrap -am spring-boot:run
```

- Base URL: `http://localhost:8080`

## Optional: jOOQ code generation flow

```bash
docker compose up -d
./mvnw -Pjooq-codegen -pl :persistence -am -DskipTests generate-sources
```

Highlight that jOOQ classes are generated from the live schema after Flyway migrations,
and sources appear under `integrations/persistence/target/generated-sources/jooq`.

## 1) Create a payroll batch (JPA write model)
(We start with the write model: transactional creation of a batch.)

```bash
curl -X POST http://localhost:8080/batches \
  -H 'Content-Type: application/json' \
  -d '{"customerId": 1001}'
```

**Expected response**
```json
{"batchId":"<uuid>"}
```

**Logging cue**
Look for the Hibernate SQL statements and bind values showing the insert for `payroll_batch`.

## 2) Add two payments (JPA write model)
(Payments are added under the aggregate, enforcing invariants)

```bash
curl -X POST http://localhost:8080/batches/<uuid>/payments \
  -H 'Content-Type: application/json' \
  -d '{"beneficiary":"Alice","amount":10.50}'

curl -X POST http://localhost:8080/batches/<uuid>/payments \
  -H 'Content-Type: application/json' \
  -d '{"beneficiary":"Bob","amount":5.25}'
```

**Expected response**
```json
{"paymentId":"<uuid>"}
```

**Logging cue**
The logs should show JPA writing the new `payroll_payment` rows along with SQL bind values.

## 3) Execute batch (transactional JPA write model)
(Execution validates the batch and updates payment statuses in one transaction)

```bash
curl -X POST http://localhost:8080/batches/<uuid>/execute
```

**Expected response**
```
HTTP/1.1 202 Accepted
```

**Logging cue**
Watch the JPA logs for the update statements that flip payment statuses.

## 4) List batches (jOOQ read model)
(Now switch to SQL-first reads: a single query with joins and aggregates)

```bash
curl http://localhost:8080/batches?page=0&size=20
```

**Expected response**
```json
[
  {
    "batchId": "<uuid>",
    "customerId": 1001,
    "status": "EXECUTED",
    "paymentsCount": 2,
    "totalAmount": 15.75,
    "createdAt": "2026-01-17T10:00:00Z",
    "updatedAt": "2026-01-17T10:05:00Z"
  }
]
```

**Logging cue**
You should see the jOOQ query in the logs, including the generated SQL for the joins and aggregation.

## Optional: JPA listing contrast
(Here’s the JPA listing endpoint: it works, but requires multiple steps to avoid paging pitfalls)

```bash
curl http://localhost:8080/batches-jpa?page=0&size=20
```

## conclusion
- JPA: best for transactional writes and domain invariants.
- jOOQ: best for reporting, paging, and SQL-first aggregation.
