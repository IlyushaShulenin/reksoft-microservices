package ru.shulenin.impl.integration.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;
import ru.shulenin.api.dto.DepartmentMessageDto;
import ru.shulenin.api.service.DepartmentSnapshotService;
import ru.shulenin.impl.integration.TestBase;
import ru.shulenin.impl.integration.annotation.IntegrationTest;
import ru.shulenin.impl.repository.DepartmentSnapshotRepository;

import static org.assertj.core.api.Assertions.assertThat;

@IntegrationTest
@RequiredArgsConstructor
class DepartmentSnapshotServiceImplTestIT extends TestBase {
    private final DepartmentSnapshotService snapshotService;
    private final DepartmentSnapshotRepository departmentSnapshotRepository;

    @Test
    @Transactional
    void save() {
        var messageDto = getDepartmentMessageDto(1L);
        snapshotService.saveOrUpdate(messageDto);

        var message = departmentSnapshotRepository.findById(messageDto.getId());
        assertThat(message).isPresent();

        var messageEntity = message.get();
        assertThat(messageEntity.getId()).isEqualTo(messageDto.getId());
        assertThat(messageEntity.getName()).isEqualTo(messageDto.getName());
    }

    @Test
    @Transactional
    void update() {
        var messageDto = getDepartmentMessageDto(3L);
        snapshotService.saveOrUpdate(messageDto);

        var updateDto = new DepartmentMessageDto(messageDto.getId(), "New name");
        snapshotService.saveOrUpdate(updateDto);

        var updateDepartment = departmentSnapshotRepository.findById(messageDto.getId());
        assertThat(updateDepartment).isPresent();
        assertThat(updateDepartment.get().getName()).isEqualTo(updateDto.getName());
    }

    @Test
    @Transactional
    void delete() {
        var messageDto = getDepartmentMessageDto(2L);
        snapshotService.saveOrUpdate(messageDto);
        snapshotService.delete(messageDto);

        var deletedMessage = departmentSnapshotRepository.findById(messageDto.getId());
        assertThat(deletedMessage).isEmpty();
    }

    private DepartmentMessageDto getDepartmentMessageDto(Long id) {
        return new DepartmentMessageDto(id, "Department");
    }
}