package ru.shulenin.impl.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.shulenin.api.controller.ConsumerFromDepartmentServiceRestController;
import ru.shulenin.api.dto.DepartmentMessageDto;
import ru.shulenin.api.service.DepartmentSnapshotService;

@RestController
@RequestMapping("/department-snapshot")
@RequiredArgsConstructor
public class ConsumerFromDepartmentServiceRestControllerImpl implements
        ConsumerFromDepartmentServiceRestController {
    private final DepartmentSnapshotService departmentSnapshotService;

    @Override
    @KafkaListener(
            id = "department-snapshot-modifying",
            topics = {"department.MODIFYING"},
            containerFactory = "singleFactory"
    )
    public void consumeDepartmentSnapshotForSaveOrUpdate(DepartmentMessageDto departmentMessageDto) {
        departmentSnapshotService.saveOrUpdate(departmentMessageDto);
    }

    @Override
    @KafkaListener(
            id = "department-snapshot-delete",
            topics = {"department.DELETE"},
            containerFactory = "singleFactory"
    )
    public void consumeDepartmentSnapshotForDelete(DepartmentMessageDto departmentMessageDto) {
        departmentSnapshotService.delete(departmentMessageDto);
    }
}
