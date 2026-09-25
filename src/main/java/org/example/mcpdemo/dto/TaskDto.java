package org.example.mcpdemo.dto;

import org.example.mcpdemo.constant.TaskStatus;

public record TaskDto(Long id, String name, TaskStatus status) {
}
