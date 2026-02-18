package com.gargoil.tmsapi.service;

import com.gargoil.tmsapi.model.Task;
import com.gargoil.tmsapi.model.User;
import com.gargoil.tmsapi.model.transporter.CreateTaskDTO;
import com.gargoil.tmsapi.model.transporter.UpdateTaskDTO;
import com.gargoil.tmsapi.repository.TaskRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository repository;
    private final AuthService authService;

    public Task getTaskById(Integer id) {
        return repository.findById(id).orElseThrow(() -> new EntityNotFoundException("Task not found"));
    }
    public Task createTask(CreateTaskDTO task) {
        User assignedTo = authService.getUserById(task.assignedTo());
        return repository.save(task.toTask(assignedTo));
    }
    public Task updateTask(Integer id, UpdateTaskDTO dto) {
        User assignedTo = authService.getUserById(dto.assignedTo());
        Task existing = getTaskById(id);
        existing.setTitle(dto.title());
        existing.setDescription(dto.description());
        existing.setStatus(dto.status());
        existing.setAssignedTo(assignedTo);
        return repository.save(existing);
    }
    public void deleteTask(Integer id) {
        repository.deleteById(id);
    }
}
