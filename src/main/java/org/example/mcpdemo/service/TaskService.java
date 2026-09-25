package org.example.mcpdemo.service;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.example.mcpdemo.constant.TaskStatus;
import org.example.mcpdemo.dto.TaskDto;
import org.example.mcpdemo.entity.TaskEntity;
import org.example.mcpdemo.exception.TaskNotFoundException;
import org.example.mcpdemo.mapper.TaskMapper;
import org.example.mcpdemo.repository.TaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Service
@Validated
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TaskService {
    public static final int NAME_MAX_LENGTH = 255;

    private final TaskRepository repository;
    private final TaskMapper mapper;

    @Transactional
    public TaskDto create(@NotBlank @Size(max = NAME_MAX_LENGTH) String name) {
        var entity = new TaskEntity();
        entity.setName(name);
        entity.setStatus(TaskStatus.NEW);
        return mapper.toDto(repository.save(entity));
    }

    public TaskDto get(Long id) {
        return mapper.toDto(findEntity(id));
    }

    @Transactional
    public TaskDto updateStatus(Long id, @NotNull TaskStatus status) {
        var entity = findEntity(id);
        entity.setStatus(status);
        return mapper.toDto(entity);
    }

    public List<TaskDto> getList() {
        return repository.findAll().stream()
                .map(mapper::toDto)
                .toList();
    }

    private TaskEntity findEntity(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));
    }
}
