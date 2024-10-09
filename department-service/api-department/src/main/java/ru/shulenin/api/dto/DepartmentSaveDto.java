package ru.shulenin.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Optional;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentSaveDto {
    @NotBlank(message = "Department name can't be empty")
    String name;

    @Positive(message = "Parent Department id must be positive")
    Long parentDepartmentId;

    public Optional<Long> getParentDepartmentId() {
        return Optional.ofNullable(parentDepartmentId);
    }

    public DepartmentSaveDto(String name) {
        this.name = name;
        this.parentDepartmentId = null;
    }
}