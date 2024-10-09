package ru.shulenin.impl.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.shulenin.api.dto.EmployeeEditDto;
import ru.shulenin.api.dto.EmployeeReadDto;
import ru.shulenin.api.dto.EmployeeSaveDto;
import ru.shulenin.api.dto.EmployeeShortInfoDto;
import ru.shulenin.api.service.EmployeeService;
import ru.shulenin.impl.client.DepartmentClient;
import ru.shulenin.impl.entity.EmployeeEntity;
import ru.shulenin.impl.exception.BadValuesInObjectException;
import ru.shulenin.impl.exception.UniqueAttributeAlreadyExistException;
import ru.shulenin.impl.mapper.DepartmentMapper;
import ru.shulenin.impl.mapper.EmployeeMapper;
import ru.shulenin.impl.repository.DepartmentSnapshotRepository;
import ru.shulenin.impl.repository.EmployeeRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EmployeeServiceImpl implements EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final DepartmentSnapshotRepository departmentRepository;

    private final DepartmentClient departmentClient;
    private final EmployeeMapper employeeMapper = EmployeeMapper.INSTANCE;
    private final DepartmentMapper departmentMapper = DepartmentMapper.INSTANCE;

    @Override
    @Transactional
    public EmployeeReadDto getEmployeeInfo(Long employeeId) {
        var foundEmployee = employeeRepository.findById(employeeId);

        if (foundEmployee.isEmpty()) {
            throw new EntityNotFoundException("Employee with id " + employeeId + " not found");
        }

        var employee = foundEmployee.get();
        var quiteDate = Optional.ofNullable(employee.getQuiteDate());

        var departmentId = employee.getDepartmentId();

        if (quiteDate.isPresent()) {
            throw new EntityNotFoundException("Employee with id " + employeeId + " not found");
        }

        var department = getDepartmentName(departmentId);

        return employeeMapper.mapEntityToRead(employee, department);
    }

    @Override
    @Transactional
    public EmployeeReadDto hireEmployee(EmployeeSaveDto employeeSaveDto) {
        var departmentId = employeeSaveDto.getDepartmentId();
        var departmentName = getDepartmentName(departmentId);

        if (!checkLeaderPayment(employeeSaveDto.getPayment(), departmentId)) {
            throw new BadValuesInObjectException("Employee's payment must be less than leader payment");
        }

        var foundByPhoneNumber = employeeRepository.findByPhoneNumber(employeeSaveDto.getPhoneNumber());

        if (foundByPhoneNumber.isPresent()) {
            throw new UniqueAttributeAlreadyExistException("Employee with this phone number already exist");
        }

        var newEmployee = employeeMapper.mapSaveToEntity(employeeSaveDto);
        var employee = employeeRepository.saveAndFlush(newEmployee);

        return employeeMapper.mapEntityToRead(employee, departmentName);
    }

    @Override
    @Transactional
    public EmployeeReadDto deleteEmployee(Long employeeId) {
        var employee = employeeRepository.findById(employeeId);

        if (employee.isEmpty()) {
            throw new EntityNotFoundException("Employee with id " + employeeId + " not found");
        }

        var employeeEntity = employee.get();
        employeeEntity.setQuiteDate(LocalDate.now());
        employeeRepository.saveAndFlush(employeeEntity);

        var departmentId = employeeEntity.getDepartmentId();
        var departmentName = getDepartmentName(departmentId);

        return employeeMapper.mapEntityToRead(employeeEntity, departmentName);
    }

    @Override
    @Transactional
    public EmployeeReadDto updateEmployee(EmployeeEditDto employeeEditDto, Long employeeId) {
        var employeeEntity = getEmployeeById(employeeId);
        var department = getDepartmentName(employeeEditDto.getDepartmentId());
        var payment = employeeEditDto.getPayment();

        checkPhoneNumberUniqueness(employeeEditDto);

        if (employeeEditDto.getIsLeader()) {
            var leaderInGroup = employeeRepository.findLeaderInDepartment(employeeEditDto.getDepartmentId());

            if (leaderInGroup.isPresent()) {
                var leader = leaderInGroup.get();
                employeeEntity.setIsLeader(true);
                leader.setIsLeader(false);
                payment = leader.getPayment();
                employeeRepository.saveAndFlush(leader);
            }
        } else {
            if (!checkLeaderPayment(employeeEditDto.getPayment(), employeeEditDto.getDepartmentId())) {
                throw new BadValuesInObjectException("Payment is not valid");
            }
        }

        var update = employeeMapper.mapEditToEntity(employeeEditDto, employeeEntity);
        update.setPayment(payment);
        employeeRepository.saveAndFlush(update);

        return employeeMapper.mapEntityToRead(update, department);
    }

    @Override
    public Integer findEmployeesCountInDepartment(Long departmentId) {
        var employees = employeeRepository.findByDepartmentId(departmentId);
        return employees.size();
    }

    @Override
    public Integer getCommonPaymentForDepartment(Long departmentId) {
        var payment = employeeRepository.commonPaymentForDepartment(departmentId);
        return payment;
    }

    @Override
    public EmployeeShortInfoDto getLeaderShortInfo(Long departmentId) {
        var employee = employeeRepository.findLeaderInDepartment(departmentId);

        if (employee.isEmpty()) {
            return new EmployeeShortInfoDto(
                    "Unknown",
                    "Unknown"
            );
        }

        var employeeEntity = employee.get();

        return new EmployeeShortInfoDto(
                employeeEntity.getName(),
                employeeEntity.getSurname()
        );
    }

    @Override
    public List<EmployeeShortInfoDto> getEmployeesInDepartment(Long departmentId) {
        var employees = employeeRepository.findByDepartmentId(departmentId);
        return employees.stream()
                .map(employeeMapper::mapEntityToShortDto)
                .toList();
    }

    private EmployeeEntity getEmployeeById(Long employeeId) {
        return employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EntityNotFoundException("Employee with id " + employeeId + " not found"));
    }

    private void checkPhoneNumberUniqueness(EmployeeEditDto employeeEditDto) throws
            UniqueAttributeAlreadyExistException {
        var phoneNumber = employeeEditDto.getPhoneNumber();
        var foundByPhoneNumber = employeeRepository.findByPhoneNumber(phoneNumber);

        if (foundByPhoneNumber.isPresent()) {
            throw new UniqueAttributeAlreadyExistException("Employee with this phone " + employeeEditDto.getPhoneNumber()
                    + "number already exist");
        }
    }

    public String getDepartmentName(Long departmentId) {
        var department = departmentRepository.findById(departmentId);

        if (department.isPresent()) {
            var name = department.get().getName();
            return name;
        }

        var departmentResponseEntity = departmentClient.findDepartmentById(departmentId);

        if (departmentResponseEntity.getStatusCode().is4xxClientError()) {
            throw new EntityNotFoundException("Department with id " + departmentId + " not found");
        }

        var departmentMessage = departmentResponseEntity.getBody();
        var departmentSnapshot = departmentMapper.mapMessageDtoToEntity(departmentMessage);
        departmentRepository.saveAndFlush(departmentSnapshot);

        return departmentMessage.getName();
    }

    private boolean checkLeaderPayment(Integer employeePayment, Long departmentId) {
        var leader = employeeRepository.findLeaderInDepartment(departmentId);

        if (leader.isEmpty()) {
            return true;
        }

        var leaderEntity = leader.get();

        return employeePayment < leaderEntity.getPayment();
    }
}
