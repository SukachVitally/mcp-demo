package org.example.mcpdemo.repository;

import org.example.mcpdemo.constant.TaskStatus;
import org.example.mcpdemo.entity.TaskEntity;
import org.springframework.data.repository.ListCrudRepository;

import java.util.List;

public interface TaskRepository extends ListCrudRepository<TaskEntity, Long> {
    List<TaskEntity> findAllByStatus(TaskStatus status);
}
