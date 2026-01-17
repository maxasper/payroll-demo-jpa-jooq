create table if not exists payroll_batch (
    id uuid primary key,
    customer_id bigint not null,
    status varchar(32) not null,
    total_amount numeric(19, 2) not null default 0,
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now()
);

create table if not exists payroll_payment (
    id uuid primary key,
    batch_id uuid not null references payroll_batch(id) on delete cascade,
    beneficiary varchar(255) not null,
    amount numeric(19, 2) not null,
    status varchar(32) not null,
    created_at timestamptz not null default now()
);

create table if not exists payroll_stats (
    batch_id uuid primary key references payroll_batch(id) on delete cascade,
    payments_cnt int not null default 0,
    total_amount numeric(19, 2) not null default 0,
    last_exec timestamptz null
);

create index if not exists idx_payroll_batch_status_created_at
    on payroll_batch (status, created_at);

create index if not exists idx_payroll_batch_customer_created_at
    on payroll_batch (customer_id, created_at);

create index if not exists idx_payroll_payment_batch_id
    on payroll_payment (batch_id);

create index if not exists idx_payroll_payment_status
    on payroll_payment (status);
