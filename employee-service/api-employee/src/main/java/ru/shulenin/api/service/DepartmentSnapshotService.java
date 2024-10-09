package ru.shulenin.api.service;

import ru.shulenin.api.dto.DepartmentMessageDto;

public interface DepartmentSnapshotService {
    void saveOrUpdate(DepartmentMessageDto departmentMessageDto);
    void delete(DepartmentMessageDto departmentMessageDto);
}
