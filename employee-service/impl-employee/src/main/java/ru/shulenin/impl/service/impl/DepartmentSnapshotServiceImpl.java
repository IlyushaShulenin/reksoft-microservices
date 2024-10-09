package ru.shulenin.impl.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.shulenin.api.dto.DepartmentMessageDto;
import ru.shulenin.api.service.DepartmentSnapshotService;
import ru.shulenin.impl.mapper.DepartmentMapper;
import ru.shulenin.impl.repository.DepartmentSnapshotRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class DepartmentSnapshotServiceImpl implements DepartmentSnapshotService {
    private final DepartmentSnapshotRepository departmentSnapshotRepository;
    private final DepartmentMapper departmentMapper = DepartmentMapper.INSTANCE;

    @Override
    public void saveOrUpdate(DepartmentMessageDto departmentMessageDto) {
        var departmentSnapshotEntity = departmentMapper.mapMessageDtoToEntity(departmentMessageDto);
        departmentSnapshotRepository.saveAndFlush(departmentSnapshotEntity);
    }

    @Override
    public void delete(DepartmentMessageDto departmentMessageDto) {
        departmentSnapshotRepository.deleteById(departmentMessageDto.getId());
    }
}
