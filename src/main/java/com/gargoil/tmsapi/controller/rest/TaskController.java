package com.gargoil.tmsapi.controller.rest;

import com.gargoil.tmsapi.controller.response.ResponseWrapper;
import com.gargoil.tmsapi.model.Task;
import com.gargoil.tmsapi.model.transporter.*;
import com.gargoil.tmsapi.service.TaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
@Slf4j
public class TaskController {
    private final TaskService taskService;

    @PostMapping
    public ResponseEntity<ResponseWrapper> createTask(@RequestBody CreateTaskDTO dto) {
        Task created = taskService.createTask(dto);
        log.info("Task created successfully with ID {}", created.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(ResponseWrapper.success(HttpStatus.CREATED, "Task created successfully", Map.of("taskId", created.getId())));
    }
    @GetMapping("/{id}")
    public ResponseEntity<ResponseWrapper> getTask(@PathVariable Integer id) {
        Task task = taskService.getTaskById(id);
        log.info("Task retrieved successfully with ID {}", task.getId());
        return ResponseEntity.ok(ResponseWrapper.success(HttpStatus.OK, "Task retrieved successfully", new TaskResponseDTO(task)));
    }
    @PutMapping("/{id}")
    public ResponseEntity<ResponseWrapper> updateTask(@PathVariable Integer id, @RequestBody UpdateTaskDTO dto) {
        Task updated = taskService.updateTask(id, dto);
        log.info("Task updated successfully with ID {}", updated.getId());
        return ResponseEntity.ok(ResponseWrapper.success(HttpStatus.OK, "Task updated successfully", Map.of("taskId", updated.getId())));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseWrapper> deleteTask(@PathVariable Integer id) {
        taskService.deleteTask(id);
        log.info("Task deleted successfully with ID {}", id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(ResponseWrapper.success(HttpStatus.NO_CONTENT, "Task deleted successfully"));
    }
}