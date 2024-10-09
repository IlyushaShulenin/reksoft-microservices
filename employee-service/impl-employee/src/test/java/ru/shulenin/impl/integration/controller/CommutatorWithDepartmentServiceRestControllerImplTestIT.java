package ru.shulenin.impl.integration.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import ru.shulenin.api.service.EmployeeService;
import ru.shulenin.impl.integration.TestBase;
import ru.shulenin.impl.integration.annotation.IntegrationTest;
import ru.shulenin.impl.integration.controller.adapter.LocalDateTypeAdapter;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@IntegrationTest
@RequiredArgsConstructor
@AutoConfigureMockMvc
class CommutatorWithDepartmentServiceRestControllerImplTestIT extends TestBase {
    private static final long DEPARTMENT_ID = 1;

    private final EmployeeService employeeService;
    private final MockMvc mockMvc;

    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, new LocalDateTypeAdapter())
            .create();

    @Test
    void getAllEmployees() throws Exception {
        var employees = gson.toJson(employeeService.getEmployeesInDepartment(DEPARTMENT_ID));
        mockMvc.perform(get("/api/v1/communication/employees/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(employees));
    }

    @Test
    void countEmployeesInDepartment() throws Exception {
        var countEmployees = gson.toJson(employeeService.getEmployeesInDepartment(DEPARTMENT_ID).size());
        mockMvc.perform(get("/api/v1/communication/employees/1/count"))
                .andExpect(status().isOk())
                .andExpect(content().json(countEmployees));
    }

    @Test
    void getCommonPaymentForDepartment() throws Exception {
        var commonPayment = gson.toJson(employeeService.getCommonPaymentForDepartment(DEPARTMENT_ID));
        mockMvc.perform(get("/api/v1/communication/employees/payment/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(commonPayment));
    }

    @Test
    void getLeaderInDepartment() throws Exception {
        var leader = gson.toJson(employeeService.getLeaderShortInfo(DEPARTMENT_ID));
        mockMvc.perform(get("/api/v1/communication/leader/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(leader));
    }
}