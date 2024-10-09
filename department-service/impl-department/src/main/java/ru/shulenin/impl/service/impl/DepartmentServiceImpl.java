package ru.shulenin.impl.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.shulenin.api.dto.DepartmentEditDto;
import ru.shulenin.api.dto.DepartmentReadDto;
import ru.shulenin.api.dto.DepartmentSaveDto;
import ru.shulenin.api.service.DepartmentService;
import ru.shulenin.api.dto.CommonPaymentForDepartment;
import ru.shulenin.api.dto.DepartmentMessageDto;
import ru.shulenin.api.dto.EmployeeShortInfoDto;
import ru.shulenin.impl.exception.DepartmentStillHasEmployeesException;
import ru.shulenin.impl.exception.UniqueAttributeAlreadyExistException;
import ru.shulenin.impl.client.EmployeeClient;
import ru.shulenin.impl.entity.DepartmentEntity;
import ru.shulenin.impl.entity.Operation;
import ru.shulenin.impl.listener.event.AuditObjectEvent;
import ru.shulenin.impl.listener.event.DepartmentMessageEvent;
import ru.shulenin.impl.mapper.DepartmentMapper;
import ru.shulenin.impl.repository.DepartmentPaymentRepository;
import ru.shulenin.impl.repository.DepartmentRepository;

import java.time.Instant;
import java.util.List;

import static ru.shulenin.impl.listener.event.DepartmentMessageEvent.MessageOperationType;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DepartmentServiceImpl implements DepartmentService {
    private static final int SCHEDULER_DURATION = 30_000;

    private final DepartmentRepository departmentRepository;
    private final DepartmentPaymentRepository departmentPaymentRepository;

    private final EmployeeClient employeeClient;
    private final DepartmentMapper departmentMapper = DepartmentMapper.INSTANCE;

    private final ApplicationEventPublisher eventPublisher;

    @Override
    public DepartmentReadDto findDepartmentById(Long departmentId) {
        var department = departmentRepository.findById(departmentId);

        if (department.isEmpty()) {
            throw new EntityNotFoundException("Department with id " + departmentId + " not found");
        }

        var departmentEntity = department.get();

        return getReadDto(departmentEntity);
    }

    @Override
    public DepartmentReadDto findDepartmentByName(String name) {
        var department = departmentRepository.findByName(name);

        if (department.isEmpty()) {
            throw new EntityNotFoundException("Department with name " + name + " not found");
        }

        var departmentEntity = department.get();

        return getReadDto(departmentEntity);
    }

    @Override
    public CommonPaymentForDepartment getPaymentForDepartment(Long departmentId) {
        var department = departmentRepository.findById(departmentId);

        if (department.isEmpty()) {
            throw new EntityNotFoundException("Department with id " + departmentId + " not found");
        }

        var departmentEntity = department.get();
        var commonPaymentResponseEntity = employeeClient.getCommonPaymentForDepartment(departmentId);
        var commonPayment = commonPaymentResponseEntity.getBody();

        return new CommonPaymentForDepartment(departmentEntity.getName(), commonPayment);
    }

    @Override
    @Transactional
    public DepartmentReadDto saveDepartment(DepartmentSaveDto departmentSaveDto) {
        var departmentName = departmentSaveDto.getName();
        var department = departmentRepository.findByName(departmentName);

        if (department.isPresent()) {
            throw new UniqueAttributeAlreadyExistException("Department with name " + departmentName + " already exists");
        }

        var newDepartment = departmentMapper.mapSaveToEntity(departmentSaveDto);

        if (departmentSaveDto.getParentDepartmentId().isPresent()) {
            var parentId = departmentSaveDto.getParentDepartmentId().get();
            var parentDepartment = departmentRepository.findById(parentId);

            if (parentDepartment.isEmpty()) {
                throw new EntityNotFoundException("Department with id " + parentId + " not found");
            }

            newDepartment = departmentRepository.saveAndFlush(newDepartment);
            departmentRepository.addChildDepartment(parentId, newDepartment.getId());
        } else {
            var mainDepartment = departmentRepository.findMainDepartment();
            mainDepartment.setIsMain(false);
            newDepartment.setIsMain(true);
            newDepartment.addChildDepartment(mainDepartment);

            departmentRepository.saveAndFlush(mainDepartment);
            departmentRepository.saveAndFlush(newDepartment);
        }


        eventPublisher.publishEvent(new AuditObjectEvent(newDepartment.getId(), Instant.now(), Operation.CREATE));
        sendMessage(newDepartment, MessageOperationType.MODIFYING);

        return getReadDto(newDepartment);
    }

    @Override
    @Transactional
    public DepartmentReadDto deleteDepartment(Long departmentId) {
        var department = departmentRepository.findById(departmentId);

        if (department.isEmpty()) {
            throw new EntityNotFoundException("Department with id " + departmentId + " does not exist");
        }

        var departmentEntity = department.get();
        var employeesInDepartment = employeeClient.getAllEmployees(departmentId)
                .getBody();

        if (!employeesInDepartment.isEmpty()) {
            throw new DepartmentStillHasEmployeesException("Department with id " + departmentId
                    + " still has employees");
        }

        if (departmentEntity.getIsMain()) {
            var nextMain = departmentEntity.removeChildDepartment(0);
            var childDepartmentsOfDeletingDepartment = departmentEntity.getChildDepartment();

            nextMain.addAllChildDepartments(childDepartmentsOfDeletingDepartment);
            nextMain.setIsMain(true);

            departmentRepository.deleteById(departmentEntity.getId());
            departmentRepository.saveAndFlush(nextMain);
        } else {
            var parentDepartment = departmentRepository.findDepartmentWithChildId(departmentId);
            var childDepartmentsOfDeletingDepartment = departmentEntity.getChildDepartment();
            parentDepartment.removeChildDepartment(departmentEntity);
            parentDepartment.addAllChildDepartments(childDepartmentsOfDeletingDepartment);
            departmentRepository.saveAndFlush(parentDepartment);
            departmentRepository.deleteById(departmentId);
        }

        eventPublisher.publishEvent(new AuditObjectEvent(departmentEntity.getId(), Instant.now(), Operation.DELETE));
        sendMessage(departmentEntity, MessageOperationType.DELETE);

        return getReadDto(departmentEntity);
    }

    @Override
    @Transactional
    public DepartmentReadDto updateDepartment(DepartmentEditDto departmentEditDto, Long departmentId) {
        var department = departmentRepository.findById(departmentId);

        if (department.isEmpty()) {
            throw new EntityNotFoundException("Department with id " + departmentId + " does not exist");
        }

        var departmentFoundByName = departmentRepository.findByName(departmentEditDto.getName());

        if (departmentFoundByName.isPresent()) {
            throw new UniqueAttributeAlreadyExistException("Department with name " + departmentEditDto.getName() + " already exists");
        }

        var departmentEntity = department.get();
        var updatedDepartment = departmentMapper.mapEditDtoToEntity(departmentEditDto, departmentEntity);
        departmentRepository.saveAndFlush(updatedDepartment);

        eventPublisher.publishEvent(new AuditObjectEvent(updatedDepartment.getId(), Instant.now(), Operation.UPDATE));
        sendMessage(updatedDepartment, MessageOperationType.MODIFYING);

        return getReadDto(updatedDepartment);
    }

    @Override
    public List<EmployeeShortInfoDto> findAllEmployeesInDepartment(Long departmentId) {
        var department = departmentRepository.findById(departmentId);

        if (department.isEmpty()) {
            throw new EntityNotFoundException("Department with id " + departmentId + " does not exist");
        }

        var employeesInDepartment = employeeClient.getAllEmployees(departmentId)
                .getBody();

        return employeesInDepartment;
    }

    @Override
    public DepartmentMessageDto findDepartmentShortInfo(Long departmentId) {
        var department = findDepartmentById(departmentId);
        return new DepartmentMessageDto(departmentId, department.getName());
    }

    @Override
    @Transactional
    @Scheduled(fixedDelay = SCHEDULER_DURATION)
    public void updatePaymentInfo() {
        var departmentsPayments = departmentPaymentRepository.findAll();

        for (var departmentPayment : departmentsPayments) {
            var id = departmentPayment.getId();
            var commonPayment = employeeClient.getCommonPaymentForDepartment(id).getBody();
            departmentPayment.setPayment(commonPayment);
        }

        departmentPaymentRepository.saveAll(departmentsPayments);
    }

    private DepartmentReadDto getReadDto(DepartmentEntity departmentEntity) {
        var employeesNumber = employeeClient.countEmployeesInDepartment(departmentEntity.getId())
                .getBody();
        var leader = employeeClient.getLeaderInDepartment(departmentEntity.getId()).getBody();
        var departmentLeaderSurname = leader.getSurname();

        return departmentMapper.mapEntityToRead(departmentEntity, departmentLeaderSurname, employeesNumber);
    }

    private void sendMessage(DepartmentEntity departmentEntity, MessageOperationType operationType) {
        var messageDto = departmentMapper.mapEntityToMessageDto(departmentEntity);
        var eventObject = new DepartmentMessageEvent(messageDto, operationType);
        eventPublisher.publishEvent(eventObject);
    }
}
