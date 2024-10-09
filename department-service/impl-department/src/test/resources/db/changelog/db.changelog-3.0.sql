--liquibase formatted sql

--changeset ilya_shulenin:1
CREATE TABLE departments.payment_in_department_info(
    department_id   BIGINT PRIMARY KEY
                    REFERENCES departments.department (id)
                    ON DELETE CASCADE,
    common_payment  INTEGER DEFAULT 0
);