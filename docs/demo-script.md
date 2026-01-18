# Demo Script (5 minutes)

## Setup

- Start the app:

```bash
./mvnw -pl integrations/bootstrap spring-boot:run
```

- Base URL: `http://localhost:8080`

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

## 3) Execute batch (transactional JPA write model)
(Execution validates the batch and updates payment statuses in one transaction)

```bash
curl -X POST http://localhost:8080/batches/<uuid>/execute
```

**Expected response**
```
HTTP/1.1 202 Accepted
```

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

## conclusion
- JPA: best for transactional writes and domain invariants.
- jOOQ: best for reporting, paging, and SQL-first aggregation.
