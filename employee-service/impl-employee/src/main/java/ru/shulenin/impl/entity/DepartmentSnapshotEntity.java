package ru.shulenin.impl.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "department_snapshot", schema = "employees")
@Getter
@Setter
@EqualsAndHashCode(exclude = {"id"})
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentSnapshotEntity {
    @Id
    private Long id;

    private String name;
}
