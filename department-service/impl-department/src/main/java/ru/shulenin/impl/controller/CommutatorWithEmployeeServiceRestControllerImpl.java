package ru.shulenin.impl.controller;

import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.shulenin.api.controller.CommutatorWithEmployeeServiceRestController;
import ru.shulenin.api.service.DepartmentService;
import ru.shulenin.api.dto.DepartmentMessageDto;

@RestController
@RequestMapping("api/v1/communication")
@RequiredArgsConstructor
public class CommutatorWithEmployeeServiceRestControllerImpl implements CommutatorWithEmployeeServiceRestController {
    private final DepartmentService departmentService;

    @Override
    public ResponseEntity<DepartmentMessageDto> getDepartmentShortInfo(
            @PathVariable("id") @Positive @Parameter(description = "Department ID") Long id
    ) {
        var department = departmentService.findDepartmentShortInfo(id);
        return new ResponseEntity<>(department, HttpStatus.OK);
    }
}
