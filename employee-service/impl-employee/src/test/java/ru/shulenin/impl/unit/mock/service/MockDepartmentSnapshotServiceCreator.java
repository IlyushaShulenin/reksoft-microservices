package ru.shulenin.impl.unit.mock.service;

import org.mockito.Mockito;
import ru.shulenin.api.service.DepartmentSnapshotService;
import ru.shulenin.impl.entity.DepartmentSnapshotEntity;
import ru.shulenin.impl.repository.DepartmentSnapshotRepository;
import ru.shulenin.impl.service.impl.DepartmentSnapshotServiceImpl;

import java.util.ArrayList;
import java.util.List;

public class MockDepartmentSnapshotServiceCreator {
    private final List<DepartmentSnapshotEntity> departments;

    public MockDepartmentSnapshotServiceCreator() {
        departments = new ArrayList<>();

        departments.add(new DepartmentSnapshotEntity(1L, "Department1"));
        departments.add(new DepartmentSnapshotEntity(2L, "Department2"));
    }

    public DepartmentSnapshotService mockDepartmentSnapshotService() {
        return new DepartmentSnapshotServiceImpl(
                mockDepartmentSnapshotRepository()
        );
    }

    public DepartmentSnapshotRepository mockDepartmentSnapshotRepository() {
        var mockRepository = Mockito.mock(DepartmentSnapshotRepository.class);

        Mockito.when(mockRepository.saveAndFlush(Mockito.any()))
                .then(invocation -> {
                    var arg = (DepartmentSnapshotEntity) invocation.getArgument(0);

                    if (arg.getId() != null) {
                        var oldDepartment = departments.stream()
                                .filter(department -> department.getId().equals(arg.getId()))
                                .findFirst()
                                .get();

                        oldDepartment.setName(arg.getName());
                    }

                    var nextId = (long) departments.size() + 1;
                    arg.setId(nextId);

                    departments.add(arg);
                    return arg;
                });

        Mockito.when(mockRepository.findById(Mockito.anyLong()))
                        .then(invocation -> {
                            var arg = (Long) invocation.getArgument(0);
                            return departments.stream()
                                    .filter(department -> department.getId().equals(arg))
                                    .findFirst();
                        });

        Mockito.doAnswer(invocation -> {
            var arg = (Long) invocation.getArgument(0);
            departments.removeIf(department -> department.getId().equals(arg));
            return null;
        }).when(mockRepository)
                .deleteById(Mockito.anyLong());

        return mockRepository;
    }
}
