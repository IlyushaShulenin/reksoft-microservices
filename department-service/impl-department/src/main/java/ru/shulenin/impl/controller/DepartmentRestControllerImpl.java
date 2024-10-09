package ru.shulenin.impl.controller;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.shulenin.api.controller.DepartmentRestController;
import ru.shulenin.api.dto.DepartmentEditDto;
import ru.shulenin.api.dto.DepartmentReadDto;
import ru.shulenin.api.dto.DepartmentSaveDto;
import ru.shulenin.api.service.DepartmentService;
import ru.shulenin.api.dto.CommonPaymentForDepartment;
import ru.shulenin.api.dto.EmployeeShortInfoDto;

import java.util.List;

@Tag(name = "Department API")
@RestController
@RequestMapping("api/v1/department")
@RequiredArgsConstructor
public class DepartmentRestControllerImpl implements DepartmentRestController {
    private final DepartmentService departmentService;

    public ResponseEntity<DepartmentReadDto> findDepartmentById(
            @PathVariable("id") @Positive @Parameter(description = "Department ID") Long id
    ) {
        var department = departmentService.findDepartmentById(id);
        return new ResponseEntity<>(department, HttpStatus.OK);
    }

    public ResponseEntity<DepartmentReadDto> findDepartmentByName(
            @PathVariable("name") @NotBlank @Parameter(description = "Department name") String name
    ) {
        var department = departmentService.findDepartmentByName(name);
        return new ResponseEntity<>(department, HttpStatus.OK);
    }

    public ResponseEntity<DepartmentReadDto> createDepartment(
            @RequestBody @Valid @Parameter(description = "DTO for creation new department") DepartmentSaveDto department
    ) {
        var savedDepartment = departmentService.saveDepartment(department);
        return new ResponseEntity<>(savedDepartment, HttpStatus.CREATED);
    }

    public ResponseEntity<DepartmentReadDto> deleteDepartment(
            @PathVariable("id") @Positive @Parameter(description = "Department id") Long id
    ) {
        var deletedDepartment = departmentService.deleteDepartment(id);
        return new ResponseEntity<>(deletedDepartment, HttpStatus.OK);
    }

    public ResponseEntity<DepartmentReadDto> updateDepartment(
            @PathVariable("id") @Positive @Parameter(description = "ID of updated department") Long id,
            @RequestBody @Valid @Parameter(description = "DTO for update") DepartmentEditDto department
    ) {
        var updatedDepartment = departmentService.updateDepartment(department, id);
        return new ResponseEntity<>(updatedDepartment, HttpStatus.OK);
    }

    public ResponseEntity<CommonPaymentForDepartment> getPayment(
            @PathVariable("id") @Positive @Parameter(description = "Department ID") Long id
    ) {
        var paymentDto = departmentService.getPaymentForDepartment(id);
        return new ResponseEntity<>(paymentDto, HttpStatus.OK);
    }

    public ResponseEntity<List<EmployeeShortInfoDto>> getEmployees(
            @PathVariable("id") @Positive @Parameter(description = "Department ID") Long id
    ) {
        var employees = departmentService.findAllEmployeesInDepartment(id);
        return new ResponseEntity<>(employees, HttpStatus.OK);
    }
}
