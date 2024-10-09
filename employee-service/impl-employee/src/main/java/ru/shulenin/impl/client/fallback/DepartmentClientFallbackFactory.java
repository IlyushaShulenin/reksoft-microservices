package ru.shulenin.impl.client.fallback;

import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import ru.shulenin.api.dto.DepartmentMessageDto;
import ru.shulenin.impl.client.DepartmentClient;

@Component
public class DepartmentClientFallbackFactory implements FallbackFactory<DepartmentClient> {
    @Override
    public DepartmentClient create(Throwable cause) {
        return new DepartmentClient() {
            private static final String DEFAULT_DEPARTMENT_NAME = "Unknown Department";

            @Override
            public ResponseEntity<DepartmentMessageDto> findDepartmentById(Long id) {
                return new ResponseEntity<>(getShortDepartment(), HttpStatus.NOT_FOUND);
            }

            private DepartmentMessageDto getShortDepartment() {
                return new DepartmentMessageDto(0L, DEFAULT_DEPARTMENT_NAME);
            }
        };
    }
}
