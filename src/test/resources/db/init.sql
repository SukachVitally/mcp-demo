truncate table task;

insert into task(name, status, created_at, updated_at)
values
    ('test_1', 'NEW'::task_status, '2026-09-22', null),
    ('test_2', 'IN_PROGRESS'::task_status, '2026-09-22', '2026-09-23'),
    ('test_3', 'DONE'::task_status, '2026-09-22', '2026-09-23');
