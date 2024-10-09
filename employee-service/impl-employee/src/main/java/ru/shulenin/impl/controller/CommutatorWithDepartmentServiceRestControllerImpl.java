package ru.shulenin.impl.controller;

import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.shulenin.api.controller.CommunicatorWithDepartmentServiceRestController;
import ru.shulenin.api.dto.EmployeeShortInfoDto;
import ru.shulenin.api.service.EmployeeService;

import java.util.List;

@RestController
@RequestMapping("api/v1/communication")
@RequiredArgsConstructor
public class CommutatorWithDepartmentServiceRestControllerImpl implements
        CommunicatorWithDepartmentServiceRestController {
    private final EmployeeService employeeService;

    @Override
    public ResponseEntity<List<EmployeeShortInfoDto>> getAllEmployees(
            @PathVariable("departmentId")
            @Positive(message = "Department id must be positive")
            @Parameter(description = "Department ID") Long departmentId
    ) {
        var employees = employeeService.getEmployeesInDepartment(departmentId);
        return new ResponseEntity<>(employees, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Integer> countEmployeesInDepartment(
            @PathVariable("departmentId")
            @Positive(message = "Department id must be positive")
            @Parameter(description = "Department ID") Long departmentId
    ) {
        var employeesInDepartment = employeeService.findEmployeesCountInDepartment(departmentId);
        return new ResponseEntity<>(employeesInDepartment, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Integer> getCommonPaymentForDepartment(
            @PathVariable("departmentId")
            @Positive(message = "Department id must be positive")
            @Parameter(description = "Department ID") Long departmentId
    ) {
        var commonPayment = employeeService.getCommonPaymentForDepartment(departmentId);
        return new ResponseEntity<>(commonPayment, HttpStatus.OK);
    }

    @Override
    @GetMapping("/leader/{departmentId}")
    public ResponseEntity<EmployeeShortInfoDto> getLeaderInDepartment(
            @PathVariable("departmentId")
            @Positive(message = "Department id must be positive")
            @Parameter(description = "Department ID") Long id
    ) {
        var employee = employeeService.getLeaderShortInfo(id);
        return new ResponseEntity<>(employee, HttpStatus.OK);
    }
}
