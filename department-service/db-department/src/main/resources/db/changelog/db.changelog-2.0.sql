--liquibase formatted sql

--changeset ilya_shulenin:1
INSERT INTO departments.department (id, name, created_at, is_main) VALUES (1, 'Main department',                 '1999-03-12', true),
                                                              (2, 'Sales Department',                '1999-03-12', false),
                                                              (3, 'Accounting',                      '1999-03-12', false),
                                                              (4, 'Human Resources Department',      '1999-05-03', false),
                                                              (5, 'IT department',                   '2001-06-05', false),
                                                              (6, 'Child department for IT',         '2012-09-10', false),
                                                              (7, 'Child department for Accounting', '2003-06-16', false);

SELECT SETVAL('departments.department_id_seq', 7);

--changeset ilya_shulenin:2
INSERT INTO departments.parent_child_departments
    (id, parent_department_id, child_department_id)
VALUES (1, 1, 2),
       (2, 1, 3),
       (3, 1, 4),
       (4, 1, 5),
       (5, 2, 3),
       (6, 5, 4),
       (7, 2, 6),
       (8, 3, 7);

SELECT SETVAL('departments.parent_child_departments_id_seq', 8);