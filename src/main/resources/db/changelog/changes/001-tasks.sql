CREATE TYPE task_status AS ENUM ('NEW', 'IN_PROGRESS', 'DONE', 'CANCELLED');

CREATE TABLE IF NOT EXISTS task (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name VARCHAR(255) not null,
    status task_status not null default 'NEW',
    created_at TIMESTAMPTZ not null default now(),
    updated_at TIMESTAMPTZ
);
