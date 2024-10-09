package ru.shulenin.impl.mapper;

import lombok.Generated;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.shulenin.api.dto.DepartmentEditDto;
import ru.shulenin.api.dto.DepartmentReadDto;
import ru.shulenin.api.dto.DepartmentSaveDto;
import ru.shulenin.api.dto.DepartmentMessageDto;
import ru.shulenin.impl.entity.DepartmentEntity;

import java.time.LocalDate;

@Generated
@Mapper(imports = LocalDate.class)
public interface DepartmentMapper {
    DepartmentMapper INSTANCE = Mappers.getMapper(DepartmentMapper.class);

    @Mapping(source = "leaderSurname", target = "leaderSurname")
    @Mapping(source = "employeesNumber", target = "employeesNumber")
    DepartmentReadDto mapEntityToRead(DepartmentEntity departmentEntity, String leaderSurname, Integer employeesNumber);

    @Mapping(expression = "java(LocalDate.now())", target = "createdAt")
    @Mapping(expression = "java(false)", target = "isMain")
    DepartmentEntity mapSaveToEntity(DepartmentSaveDto departmentSaveDto);

    @Mapping(source = "departmentEditDto.name", target = "name")
    @Mapping(source = "departmentEntity.id", target = "id")
    @Mapping(source = "departmentEntity.createdAt", target = "createdAt")
    @Mapping(source = "departmentEntity.isMain", target = "isMain")
    @Mapping(source = "departmentEntity.childDepartment", target = "childDepartment")
    DepartmentEntity mapEditDtoToEntity(DepartmentEditDto departmentEditDto, DepartmentEntity departmentEntity);

    DepartmentMessageDto mapEntityToMessageDto(DepartmentEntity entity);
}
