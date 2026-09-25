package org.example.mcpdemo.mapper;

import org.example.mcpdemo.dto.TaskDto;
import org.example.mcpdemo.entity.TaskEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TaskMapper {
    TaskDto toDto(TaskEntity entity);
}
