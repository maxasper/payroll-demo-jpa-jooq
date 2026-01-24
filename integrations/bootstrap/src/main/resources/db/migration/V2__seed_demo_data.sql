create extension if not exists pgcrypto;

with seed as (
    select
        gs as batch_index,
        gen_random_uuid() as batch_id,
        1000 + gs as customer_id,
        'NEW' as status,
        (30 * gs + 6)::numeric(19, 2) as total_amount,
        now() - (gs || ' days')::interval as created_at,
        now() - (gs || ' days')::interval as updated_at
    from generate_series(1, 50) gs
),
insert_batches as (
    insert into payroll_batch (id, customer_id, status, total_amount, created_at, updated_at)
    select batch_id, customer_id, status, total_amount, created_at, updated_at
    from seed
)
insert into payroll_payment (id, batch_id, beneficiary, amount, status, created_at)
select
    gen_random_uuid(),
    seed.batch_id,
    'Beneficiary ' || seed.batch_index || '-' || payment_index,
    (seed.batch_index * 10 + payment_index)::numeric(19, 2),
    'NEW',
    seed.created_at
from seed
cross join generate_series(1, 3) as payment_index;
