package ru.shulenin.impl.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.shulenin.impl.entity.DepartmentEntity;

import java.util.Optional;

@Repository("departmentRepository")
public interface DepartmentRepository extends JpaRepository<DepartmentEntity, Long> {
    Optional<DepartmentEntity> findByName(String name);

    @Query(
            value = """
                    INSERT INTO departments.parent_child_departments (parent_department_id, child_department_id) 
                    VALUES (:departmentId, :childId)
            """,
            nativeQuery = true
    )
    @Modifying
    void addChildDepartment(@Param("departmentId") Long departmentId, @Param("childId") Long childId);

    @Query(value = "select d from DepartmentEntity d where d.isMain = true")
    DepartmentEntity findMainDepartment();

    @Query(
//            value = """
//                        SELECT d.* FROM departments.department d
//                        JOIN departments.parent_child_departments pc ON pc.parent_department_id = d.id
//                                 WHERE pc.child_department_id = :childId
//                        """,
            value = "select d from DepartmentEntity d join d.childDepartment cd where cd.id = :childId"
    )
    DepartmentEntity findDepartmentWithChildId(@Param("childId") Long childId);
}
