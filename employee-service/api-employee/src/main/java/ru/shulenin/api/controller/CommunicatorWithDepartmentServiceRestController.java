package ru.shulenin.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.shulenin.api.dto.EmployeeShortInfoDto;

import java.util.List;

public interface CommunicatorWithDepartmentServiceRestController {
    @Operation(
            summary = "Get list of all employees in department",
            description = "Get list of all employees in department"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List is got"),
            @ApiResponse(responseCode = "404", description = "Department is not found")
    })
    @GetMapping("/employees/{departmentId}")
    ResponseEntity<List<EmployeeShortInfoDto>> getAllEmployees(
            @PathVariable("departmentId")
            @Positive(message = "Department id must be positive")
            @Parameter(description = "Department ID") Long departmentId
    );

    @Operation(
            summary = "Get employees number in department",
            description = "Get the number of workers in the department given by ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Number of workers received"),
            @ApiResponse(responseCode = "404", description = "Department is not found")
    })
    @GetMapping("/employees/{departmentId}/count")
    ResponseEntity<Integer> countEmployeesInDepartment(
            @PathVariable("departmentId")
            @Positive(message = "Department id must be positive")
            @Parameter(description = "Department ID") Long departmentId
    );

    @Operation(
            summary = "Obtaining information about the payroll fund in the department",
            description = "Obtaining information about the salary fund in the department obtained by ID"
    )
    @ApiResponses(value ={
            @ApiResponse(responseCode = "200", description = "Payment info is getting"),
            @ApiResponse(responseCode = "404", description = "Department not found")
    })
    @GetMapping("/employees/payment/{departmentId}")
    ResponseEntity<Integer> getCommonPaymentForDepartment(
            @PathVariable("departmentId")
            @Positive(message = "Department id must be positive")
            @Parameter(description = "Department ID") Long departmentId
    );

    @Operation(
            summary = "Get a department leader",
            description = "Information about the leader in the specified department"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Leader is getting"),
            @ApiResponse(responseCode = "404", description = "Department not found")
    })
    @GetMapping("/leader/{departmentId}")
    ResponseEntity<EmployeeShortInfoDto> getLeaderInDepartment(
            @PathVariable("departmentId")
            @Positive(message = "Department id must be positive")
            @Parameter(description = "Department ID") Long id
    );
}
