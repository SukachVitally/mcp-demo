package org.example.mcpdemo.mcp;

import lombok.RequiredArgsConstructor;
import org.example.mcpdemo.constant.TaskStatus;
import org.example.mcpdemo.dto.TaskDto;
import org.example.mcpdemo.service.TaskService;
import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class TaskMcpTool {
    private final TaskService service;

    @McpTool(name = "add_task", description = "Create a new task with status NEW and return it")
    public TaskDto add(@McpToolParam(description = "Task name, 1-255 characters") String name) {
        return service.create(name);
    }

    @McpTool(name = "get_task", description = "Get a task by its id. Fails if the task does not exist")
    public TaskDto get(@McpToolParam(description = "Task id") Long id) {
        return service.get(id);
    }

    @McpTool(name = "update_task_status",
            description = "Change the status of a task and return the updated task. Fails if the task does not exist")
    public TaskDto updateStatus(@McpToolParam(description = "Task id") Long id,
                                @McpToolParam(description = "New task status") TaskStatus status) {
        return service.updateStatus(id, status);
    }

    @McpTool(name = "get_task_list", description = "List all tasks")
    public List<TaskDto> getList() {
        return service.getList();
    }
}
