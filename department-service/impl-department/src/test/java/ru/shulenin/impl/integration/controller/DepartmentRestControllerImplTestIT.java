package ru.shulenin.impl.integration.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import ru.shulenin.api.dto.DepartmentEditDto;
import ru.shulenin.api.dto.DepartmentSaveDto;
import ru.shulenin.api.service.DepartmentService;
import ru.shulenin.impl.integration.TestBase;
import ru.shulenin.impl.integration.annotation.IntegrationTest;
import ru.shulenin.impl.integration.controller.adapter.LocalDateTypeAdapter;

import java.time.LocalDate;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@IntegrationTest
@RequiredArgsConstructor
@AutoConfigureMockMvc
class DepartmentRestControllerImplTestIT extends TestBase {
    private static final String DEPARTMENT = "/api/v1/department";
    private static final String DEPARTMENTS_ID = "/api/v1/department/{id}";

    private final MockMvc mockMvc;
    private final DepartmentService departmentService;

    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, new LocalDateTypeAdapter())
            .create();

    @Test
    void findDepartmentById() throws Exception {
        mockMvc.perform(get(DEPARTMENTS_ID, 1L))
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void findUnknownDepartmentId() throws Exception {
        mockMvc.perform(get(DEPARTMENTS_ID, 100L))
                .andExpect(status().isNotFound());
    }

    @Test
    void findDepartmentByName() throws Exception {
        mockMvc.perform(get("/api/v1/department/name/{name}", "Main department"))
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void findUnknownDepartmentByName() throws Exception {
        mockMvc.perform(get("/api/v1/department/name/{name}", "Unknown department"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createDepartment() throws Exception {
        var saveDto = gson.toJson(getDepartmentSaveDto());
        mockMvc.perform(post(DEPARTMENT)
                        .contentType(APPLICATION_JSON)
                        .content(saveDto))
                .andExpect(status().isCreated());
    }

    @Test
    void saveExistentDepartment() throws Exception {
        var saveDto = gson.toJson(getExistentDepartment());
        mockMvc.perform(post(DEPARTMENT)
                        .contentType(APPLICATION_JSON)
                        .content(saveDto))
                .andExpect(status().isBadRequest());
    }

    @Test
    void saveDepartmentWithUnknownParentDepartment() throws Exception {
        var saveDto = gson.toJson(getDepartmentSaveDtoWithUnknownParent());
        mockMvc.perform(post(DEPARTMENT)
                        .content(saveDto)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteDepartment() throws Exception {
        mockMvc.perform(delete(DEPARTMENTS_ID, 1L))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteEmptyDepartment() throws Exception {
        mockMvc.perform(delete(DEPARTMENTS_ID, 7L))
                .andExpect(status().isOk());
    }

    @Test
    void deleteUnRegisteredDepartment() throws Exception {
        mockMvc.perform(delete(DEPARTMENTS_ID, 100L))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateDepartment() throws Exception {
        var editDto = gson.toJson(getDepartmentEditDto());
        mockMvc.perform(put(DEPARTMENTS_ID, 1L)
                        .contentType(APPLICATION_JSON)
                        .content(editDto))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON));
    }

    @Test
    void updateUnknownDepartment() throws Exception {
        var editDto = gson.toJson(getDepartmentEditDto());
        mockMvc.perform(put(DEPARTMENTS_ID, 100L)
                        .contentType(APPLICATION_JSON)
                        .content(editDto))
                .andExpect(status().isNotFound());
    }

    @Test
    void getPayment() throws Exception {
        var payment = gson.toJson(departmentService.getPaymentForDepartment(1L));
        mockMvc.perform(get("/api/v1/department/1/payment"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(content().json(payment));
    }

    @Test
    void getEmployees() throws Exception {
        var employees = gson.toJson(departmentService.findAllEmployeesInDepartment(1L));
        mockMvc.perform(get("/api/v1/department/1/employees"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(content().json(employees));
    }

    private DepartmentSaveDto getDepartmentSaveDto() {
        return new DepartmentSaveDto("Test");
    }

    private DepartmentSaveDto getExistentDepartment() {
        return new DepartmentSaveDto("Accounting");
    }

    private DepartmentSaveDto getDepartmentSaveDtoWithUnknownParent() {
        return new DepartmentSaveDto("Test_", 100L);
    }

    private DepartmentEditDto getDepartmentEditDto() {
        return new DepartmentEditDto("_test");
    }
}