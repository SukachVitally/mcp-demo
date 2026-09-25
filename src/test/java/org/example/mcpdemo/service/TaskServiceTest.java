package org.example.mcpdemo.service;

import jakarta.validation.ConstraintViolationException;
import org.example.mcpdemo.BaseFunctionalTest;
import org.example.mcpdemo.constant.TaskStatus;
import org.example.mcpdemo.entity.TaskEntity;
import org.example.mcpdemo.exception.TaskNotFoundException;
import org.example.mcpdemo.repository.TaskRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TaskServiceTest extends BaseFunctionalTest {

    private static final String NEW_TEST_NAME = "test_1";
    private static final long UNKNOWN_ID = -1L;

    @Autowired
    private TaskService taskService;

    @Autowired
    private TaskRepository taskRepository;

    @Test
    void createTask() {
        var dto = taskService.create("test");

        assertThat(dto.id()).isNotNull();
        assertThat(dto.status()).isEqualTo(TaskStatus.NEW);
        assertThat(taskRepository.findById(dto.id())).get()
                .satisfies(e -> assertThat(e.getCreatedAt()).isNotNull());
        assertThat(taskService.getList()).hasSize(4);
    }

    @Test
    void createTaskWithDuplicateName() {
        taskService.create(NEW_TEST_NAME);

        assertThat(taskService.getList()).hasSize(4);
    }

    @Test
    void createTaskWithBlankNameFails() {
        assertThatThrownBy(() -> taskService.create(" "))
                .isInstanceOf(ConstraintViolationException.class);
    }

    @Test
    void createTaskWithTooLongNameFails() {
        var name = "a".repeat(TaskService.NAME_MAX_LENGTH + 1);

        assertThatThrownBy(() -> taskService.create(name))
                .isInstanceOf(ConstraintViolationException.class);
    }

    @Test
    void getTask() {
        var entity = getTaskEntity(NEW_TEST_NAME);
        var dto = taskService.get(entity.getId());

        assertThat(dto.status()).isEqualTo(TaskStatus.NEW);
    }

    @Test
    void getUnknownTaskFails() {
        assertThatThrownBy(() -> taskService.get(UNKNOWN_ID))
                .isInstanceOf(TaskNotFoundException.class);
    }

    @Test
    void updateTaskStatus() {
        var entity = getTaskEntity(NEW_TEST_NAME);
        var updated = taskService.updateStatus(entity.getId(), TaskStatus.DONE);

        assertThat(updated.status()).isEqualTo(TaskStatus.DONE);
        assertThat(taskService.get(entity.getId()).status()).isEqualTo(TaskStatus.DONE);
    }

    @Test
    void updateUnknownTaskStatusFails() {
        assertThatThrownBy(() -> taskService.updateStatus(UNKNOWN_ID, TaskStatus.DONE))
                .isInstanceOf(TaskNotFoundException.class);
    }

    @Test
    void getTaskList() {
        assertThat(taskService.getList()).hasSize(3);
    }

    private TaskEntity getTaskEntity(String name) {
        return taskRepository.findAll().stream()
                .filter(e -> e.getName().equals(name))
                .findFirst()
                .orElseThrow();
    }
}
