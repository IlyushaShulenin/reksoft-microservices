package ru.shulenin.api.controller;

import org.springframework.web.bind.annotation.PostMapping;
import ru.shulenin.api.dto.DepartmentMessageDto;

public interface ConsumerFromDepartmentServiceRestController {
    @PostMapping("/modifying")
    void consumeDepartmentSnapshotForSaveOrUpdate(DepartmentMessageDto departmentMessageDto);

    @PostMapping("/delete")
    void consumeDepartmentSnapshotForDelete(DepartmentMessageDto departmentMessageDto);
}
