package ru.shulenin.impl.client.fallback;

import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import ru.shulenin.api.dto.EmployeeShortInfoDto;
import ru.shulenin.impl.client.EmployeeClient;

import java.util.Collections;
import java.util.List;

@Component
public class EmployeeClientFallbackFactory implements FallbackFactory<EmployeeClient> {
    @Override
    public EmployeeClient create(Throwable cause) {
        return new EmployeeClient() {
            @Override
            public ResponseEntity<Integer> getCommonPaymentForDepartment(Long departmentId) {
                return new ResponseEntity<>(0, HttpStatus.NOT_FOUND);
            }

            @Override
            public ResponseEntity<Integer> countEmployeesInDepartment(Long departmentId) {
                return new ResponseEntity<>(0, HttpStatus.NOT_FOUND);
            }

            @Override
            public ResponseEntity<EmployeeShortInfoDto> getLeaderInDepartment(Long departmentId) {
                return new ResponseEntity<>(getShortDot(), HttpStatus.NOT_FOUND);
            }

            @Override
            public ResponseEntity<List<EmployeeShortInfoDto>> getAllEmployees(Long departmentId) {
                return new ResponseEntity<>(Collections.emptyList(), HttpStatus.NOT_FOUND);
            }

            private EmployeeShortInfoDto getShortDot() {
                return new EmployeeShortInfoDto(
                        "Unknown",
                        "Unknown"
                );
            }
        };
    }
}
