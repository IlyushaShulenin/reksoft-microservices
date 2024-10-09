package ru.shulenin.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.shulenin.api.dto.EmployeeEditDto;
import ru.shulenin.api.dto.EmployeeReadDto;
import ru.shulenin.api.dto.EmployeeSaveDto;

public interface EmployeeRestController {
    @Operation(
            summary = "Get information about employee",
            description = "Get information about employee by id"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Employee found successfully"),
            @ApiResponse(responseCode = "404", description = "Employee not found")
    })
    @GetMapping("employee/{id}")
    ResponseEntity<EmployeeReadDto> getEmployee(
            @PathVariable("id")
            @Positive(message = "Employee id must be positive")
            @Parameter(description = "Employee ID") Long id
    );

    @Operation(
            summary = "Hire a worker",
            description = "Hire a new worker with department indication"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Employee saved successfully"),
            @ApiResponse(responseCode = "404", description = "Department not found"),
            @ApiResponse(responseCode = "400", description = "Phone number already registered")
    })
    @PostMapping("employee")
    ResponseEntity<EmployeeReadDto> saveEmployee(
            @RequestBody @Valid @Parameter(description = "Save DTO") EmployeeSaveDto employeeSaveDto
    );

    @Operation(
            summary = "Delete an employee",
            description = "Employee deleting by ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Employee delete successfully"),
            @ApiResponse(responseCode = "404", description = "Employee not found")
    })
    @DeleteMapping("employee/{id}")
    ResponseEntity<EmployeeReadDto> deleteEmployee(
            @PathVariable("id") @Positive(message = "Employee id must be positive") Long id
    );

    @Operation(
            summary = "Update employee info",
            description = "Changing information about a worker, the ability to make him " +
                    "a department leader or move him to another department"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Employee update successfully"),
            @ApiResponse(responseCode = "404", description = "Employee not found"),
            @ApiResponse(responseCode = "400", description = "Phone number already registered")
    })
    @PutMapping("employee/{id}")
    ResponseEntity<EmployeeReadDto> updateEmployee(
            @PathVariable("id")
            @Positive(message = "Employee id must be positive")
            @Parameter(description = "Employee ID") Long id,
            @RequestBody @Valid @Parameter(description = "Employee update DTO") EmployeeEditDto employeeEditDto
    );
}
