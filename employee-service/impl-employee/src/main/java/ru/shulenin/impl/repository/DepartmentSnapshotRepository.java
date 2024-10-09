package ru.shulenin.impl.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.shulenin.impl.entity.DepartmentSnapshotEntity;

public interface DepartmentSnapshotRepository extends JpaRepository<DepartmentSnapshotEntity, Long> {
}
