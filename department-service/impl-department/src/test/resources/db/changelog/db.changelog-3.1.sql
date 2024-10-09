--liquibase formatted sql

--changeset ilya_shulenin:1
INSERT INTO departments.payment_in_department_info(department_id)
    SELECT d.id FROM departments.department d;