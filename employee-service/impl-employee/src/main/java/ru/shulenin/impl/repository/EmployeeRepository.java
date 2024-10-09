package ru.shulenin.impl.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.shulenin.impl.entity.EmployeeEntity;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<EmployeeEntity, Long> {
    @Query(
            nativeQuery = true,
            value = """
                SELECT e.* FROM employees.employee AS e
                WHERE e.department_id = :departmentId AND e.is_leader = true
            """
    )
    Optional<EmployeeEntity> findLeaderInDepartment(@Param("departmentId") Long departmentId);

    @Query(value = "select e from EmployeeEntity e where e.id = :id and e.quiteDate is null")
    Optional<EmployeeEntity> findById(@Param("id") Long id);

    @Query(value = "select e from EmployeeEntity e where e.quiteDate is null and e.departmentId = :departmentId")
    List<EmployeeEntity> findByDepartmentId(@Param("departmentId") Long departmentId);

    Optional<EmployeeEntity> findByPhoneNumber(String phoneNumber);

    @Query(
            value = "SELECT sum(e.payment) FROM employees.employee e WHERE e.department_id = :departmentId",
            nativeQuery = true
    )
    Integer commonPaymentForDepartment(@Param("departmentId") Long departmentId);
}
