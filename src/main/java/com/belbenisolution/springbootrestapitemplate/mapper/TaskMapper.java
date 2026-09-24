package com.belbenisolution.springbootrestapitemplate.mapper;

import com.belbenisolution.springbootrestapitemplate.dto.TaskRequest;
import com.belbenisolution.springbootrestapitemplate.dto.TaskResponse;
import com.belbenisolution.springbootrestapitemplate.entity.Task;
import org.springframework.stereotype.Component;

@Component
public class TaskMapper {

    public Task toEntity(TaskRequest request) {
        Task task = new Task();
        updateEntity(task, request);
        return task;
    }

    public void updateEntity(Task task, TaskRequest request) {
        task.setTitle(request.title());
        task.setDescription(request.description());
        task.setDone(request.done());
    }

    public TaskResponse toResponse(Task task) {
        return new TaskResponse(task.getId(), task.getTitle(),
                task.getDescription(), task.isDone(), task.getCreatedAt());
    }
}
