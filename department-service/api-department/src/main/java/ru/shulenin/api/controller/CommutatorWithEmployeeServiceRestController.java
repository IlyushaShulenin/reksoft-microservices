package ru.shulenin.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.shulenin.api.dto.DepartmentMessageDto;

public interface CommutatorWithEmployeeServiceRestController {
    @Operation(
            summary = "Getting short info about department",
            description = "Find department by ID and get short info about it"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Department found"),
            @ApiResponse(responseCode = "404", description = "Department not found")
    })
    @GetMapping("/short/{id}")
    ResponseEntity<DepartmentMessageDto> getDepartmentShortInfo(
            @PathVariable("id") @Positive @Parameter(description = "Department ID") Long id
    );
}
