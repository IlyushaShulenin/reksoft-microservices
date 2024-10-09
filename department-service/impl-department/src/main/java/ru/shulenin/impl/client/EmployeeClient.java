package ru.shulenin.impl.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.shulenin.api.dto.EmployeeShortInfoDto;
import ru.shulenin.impl.client.fallback.EmployeeClientFallbackFactory;

import java.util.List;

@FeignClient(
        value = "employee-service",
        fallbackFactory = EmployeeClientFallbackFactory.class
)
@CircuitBreaker(name = "default")
public interface EmployeeClient {

    @GetMapping("/api/v1/communication/employees/payment/{departmentId}")
    ResponseEntity<Integer> getCommonPaymentForDepartment(@PathVariable("departmentId") Long departmentId);

    @GetMapping("/api/v1/communication/employees/{departmentId}/count")
    ResponseEntity<Integer> countEmployeesInDepartment(@PathVariable("departmentId") Long departmentId);

    @GetMapping("/api/v1/communication/leader/{departmentId}")
    ResponseEntity<EmployeeShortInfoDto> getLeaderInDepartment(@PathVariable("departmentId") Long departmentId);

    @GetMapping("/api/v1/communication/employees/{departmentId}")
    ResponseEntity<List<EmployeeShortInfoDto>> getAllEmployees(@PathVariable("departmentId") Long departmentId);
}
