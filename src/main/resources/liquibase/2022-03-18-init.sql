create table "user"(
    id uuid primary key,
    email varchar unique not null,
    name varchar not null,
    surname varchar not null
);

create unique index user_email_uidx on "user"(email);

create table user_credentials(
    id uuid primary key references "user"(id),
    password varchar not null
);

create table plan(
    id uuid primary key,
    title varchar not null,
    user_id uuid not null references "user"(id),
    create_dt timestamp with time zone not null,
    due_to timestamp with time zone not null
);

create index plan_user_id_idx on plan(user_id);

create table plan_access(
    plan_id uuid references plan(id) on delete cascade not null,
    user_id uuid references "user"(id) not null,
    type varchar not null,
    primary key(plan_id, user_id)
);

create table task(
    id uuid primary key,
    title varchar not null,
    description varchar not null,
    acceptance_criteria varchar not null,
    plan_id uuid references plan(id) on delete cascade not null,
    status varchar not null,
    create_dt timestamp with time zone not null,
    due_to timestamp with time zone not null
);

create index task_plan_id_idx on task(plan_id);
create index task_create_dt_idx on task(create_dt);

create table comment(
    id uuid primary key,
    text varchar not null,
    task_id uuid references task(id) on delete cascade not null,
    user_id uuid references "user"(id) not null,
    create_dt timestamp with time zone not null,
    update_dt timestamp with time zone not null
);

create index comment_task_id_idx on comment(task_id);

create table task_audit(
    task_id uuid references task(id) on delete cascade not null,
    status varchar not null,
    date_time timestamp with time zone not null,
    primary key(date_time, task_id)
);

create table notification_settings(
    user_id uuid primary key references "user"(id),
    enabled boolean not null,
    days_before_deadline int not null,
    days_before_report int not null
);

create index user_notifications_user_id_enabled_idx on notification_settings(user_id, enabled);

create table feedback_request(
    id uuid primary key,
    requester_id uuid references "user"(id) not null,
    assignee_id uuid references "user"(id) not null,
    task_id uuid references task(id) on delete cascade not null ,
    create_dt timestamp with time zone not null
);

create index feedback_request_task_id_assignee_id_idx on feedback_request(task_id, assignee_id);

create table task_feedback(
    id uuid primary key,
    request_id uuid references feedback_request(id) on delete cascade not null,
    text varchar not null,
    create_dt timestamp with time zone not null
);
