--liquibase formatted sql

--changeset ilya_shulenin:1
CREATE TABLE departments.audit_department(
    id              BIGSERIAL PRIMARY KEY,
    department_id   BIGINT NOT NULL,
    timestamp       TIMESTAMP NOT NULL,
    operation       VARCHAR(16) NOT NULL
);