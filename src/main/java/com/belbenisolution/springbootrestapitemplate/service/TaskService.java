package com.belbenisolution.springbootrestapitemplate.service;

import com.belbenisolution.springbootrestapitemplate.dto.TaskRequest;
import com.belbenisolution.springbootrestapitemplate.dto.TaskResponse;
import com.belbenisolution.springbootrestapitemplate.entity.Task;
import com.belbenisolution.springbootrestapitemplate.exception.ResourceNotFoundException;
import com.belbenisolution.springbootrestapitemplate.mapper.TaskMapper;
import com.belbenisolution.springbootrestapitemplate.repository.TaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TaskService {

    private final TaskRepository repository;
    private final TaskMapper mapper;

    public TaskService(TaskRepository repository, TaskMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public List<TaskResponse> findAll() {
        return repository.findAll().stream().map(mapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public TaskResponse findById(Long id) {
        return mapper.toResponse(getTaskOrThrow(id));
    }

    public TaskResponse create(TaskRequest request) {
        return mapper.toResponse(repository.save(mapper.toEntity(request)));
    }

    public TaskResponse update(Long id, TaskRequest request) {
        Task task = getTaskOrThrow(id);
        mapper.updateEntity(task, request);
        return mapper.toResponse(task);
    }

    public void delete(Long id) {
        repository.delete(getTaskOrThrow(id));
    }

    private Task getTaskOrThrow(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task with ID not found " + id));
    }
}
