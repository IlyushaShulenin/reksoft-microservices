--liquibase formatted sql

--changeset ilya_shulenin:1
CREATE OR REPLACE FUNCTION insert_into_department_payment()
    RETURNS TRIGGER AS $$
BEGIN
    INSERT INTO departments.payment_in_department_info(department_id)
    SELECT d.id FROM departments.department d
    WHERE d.id = NEW.id;

    RETURN NULL;
END
    $$ language plpgsql;

--changeset ilya_shulenin:2
CREATE TRIGGER insert_into_department_payment_after_insertion_into_department
    AFTER INSERT ON departments.department
    FOR EACH ROW EXECUTE PROCEDURE insert_into_department_payment();