package ru.shulenin.impl.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.shulenin.api.dto.DepartmentMessageDto;
import ru.shulenin.impl.client.fallback.DepartmentClientFallbackFactory;

@FeignClient(
        value = "department-service",
        fallbackFactory = DepartmentClientFallbackFactory.class
)
public interface DepartmentClient {
    @GetMapping("/api/v1/communication/short/{id}")
    ResponseEntity<DepartmentMessageDto> findDepartmentById(@PathVariable("id") Long id);
}
