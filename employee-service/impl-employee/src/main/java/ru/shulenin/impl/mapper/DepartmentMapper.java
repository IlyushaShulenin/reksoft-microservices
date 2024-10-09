package ru.shulenin.impl.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import ru.shulenin.api.dto.DepartmentMessageDto;
import ru.shulenin.impl.entity.DepartmentSnapshotEntity;

@Mapper
public interface DepartmentMapper {
    DepartmentMapper INSTANCE = Mappers.getMapper(DepartmentMapper.class);

    DepartmentSnapshotEntity mapMessageDtoToEntity(DepartmentMessageDto dto);
}
