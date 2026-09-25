package org.example.mcpdemo.exception;

public class TaskNotFoundException extends ApplicationException {
    public TaskNotFoundException(Long id) {
        super(String.format("Task with id %s is not found", id));
    }
}
