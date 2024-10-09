--liquibase formatted sql

--changeset ilya_shulenin:1
CREATE TABLE departments.department(
    id           BIGSERIAL PRIMARY KEY,
    name         VARCHAR(256) NOT NULL UNIQUE,
    created_at   DATE NOT NULL,
    is_main      BOOLEAN NOT NULL
);

--changeset ilya_shulenin:2
CREATE TABLE departments.parent_child_departments (
    id                       BIGSERIAL PRIMARY KEY,
    parent_department_id     BIGINT REFERENCES departments.department(id) ON DELETE CASCADE,
    child_department_id      BIGINT REFERENCES departments.department(id) ON DELETE CASCADE,

    CONSTRAINT parent_child_unique UNIQUE (parent_department_id, child_department_id)
);
