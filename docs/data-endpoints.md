# Data & Endpoint Reference

This reference explains the demo database tables and HTTP endpoints, along with the controllers and persistence technology (JPA or jOOQ) each one exercises.
For the step-by-step walkthrough, see `docs/demo-script.md`.

## Database Tables

### `payroll_batch`
- **Purpose**: Aggregate root for a payroll batch in the JPA write model.
- **Lifecycle**: Created by `POST /batches`, updated when payments are added or executed.
- **Key columns**: `status`, `total_amount`, `created_at`, `updated_at`.

### `payroll_payment`
- **Purpose**: Child rows for each payment in a batch (JPA write model).
- **Lifecycle**: Created by `POST /batches/{batchId}/payments`, status updated during execution.
- **Key columns**: `beneficiary`, `amount`, `status`, `created_at`.

### `payroll_stats`
- **Purpose**: Read/reporting table maintained by jOOQ for batch-level aggregates.
- **Lifecycle**: Upserted after execution to capture payment counts, totals, and last execution time.
- **Key columns**: `payments_cnt`, `total_amount`, `last_exec`.

## Controllers

### `PayrollBatchController`
- **Purpose**: Handles transactional write flows for batch creation, payment additions, and execution.
- **Technology**: JPA write model for batch/payment updates; execution also triggers a jOOQ stats upsert.
- **Endpoints**: `POST /batches`, `POST /batches/{batchId}/payments`, `POST /batches/{batchId}/execute`.

### `PayrollBatchReadController`
- **Purpose**: Provides SQL-first batch listing for reporting and paging.
- **Technology**: jOOQ read model for aggregations and joins.
- **Endpoint**: `GET /batches`.

### `PayrollBatchJpaController`
- **Purpose**: Shows a JPA-based listing approach for comparison with the jOOQ query.
- **Technology**: JPA read model with multi-step paging.
- **Endpoint**: `GET /batches-jpa`.

## HTTP Endpoints

### Write Model (JPA)

#### `POST /batches`
- **Purpose**: Create a new payroll batch.
- **Controller**: `PayrollBatchController` (JPA write model).
- **Writes**: `payroll_batch`.

#### `POST /batches/{batchId}/payments`
- **Purpose**: Add a payment to a batch while enforcing invariants.
- **Controller**: `PayrollBatchController` (JPA write model).
- **Writes**: `payroll_payment`, updates `payroll_batch.total_amount`.

#### `POST /batches/{batchId}/execute`
- **Purpose**: Validate and execute the batch in one transaction.
- **Controller**: `PayrollBatchController` (JPA write model with jOOQ stats upsert).
- **Writes**: Updates `payroll_batch` and `payroll_payment` statuses, upserts `payroll_stats` via jOOQ.

### Read Model (jOOQ)

#### `GET /batches`
- **Purpose**: SQL-first batch listing with joins, aggregates, and paging.
- **Controller**: `PayrollBatchReadController` (jOOQ read model).
- **Reads**: `payroll_batch` + `payroll_payment`.
- **Query params**: `status`, `customerId`, `page`, `size`, `sort`.

### Read Model (JPA contrast)

#### `GET /batches-jpa`
- **Purpose**: JPA-based listing that demonstrates the multi-step paging workaround.
- **Controller**: `PayrollBatchJpaController` (JPA read model).
- **Reads**: `payroll_batch` + `payroll_payment`.
- **Query params**: `status`, `customerId`, `page`, `size`, `sort`.
