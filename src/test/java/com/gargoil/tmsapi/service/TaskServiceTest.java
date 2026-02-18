package com.gargoil.tmsapi.service;

import com.gargoil.tmsapi.model.Task;
import com.gargoil.tmsapi.model.User;
import com.gargoil.tmsapi.model.transporter.CreateTaskDTO;
import com.gargoil.tmsapi.model.transporter.UpdateTaskDTO;
import com.gargoil.tmsapi.repository.TaskRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository repository;

    @Mock
    private AuthService authService;

    @InjectMocks
    private TaskService taskService;

    @Test
    void getTaskById_success() {
        Task task = new Task();
        task.setId(1);

        when(repository.findById(1)).thenReturn(Optional.of(task));

        Task result = taskService.getTaskById(1);

        assertEquals(1, result.getId());
    }

    @Test
    void getTaskById_notFound_throwsException() {
        when(repository.findById(1)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> taskService.getTaskById(1));
    }

    @Test
    void createTask_success() {
        CreateTaskDTO dto = mock(CreateTaskDTO.class);
        User user = new User();
        Task task = new Task();

        when(dto.assignedTo()).thenReturn(10);
        when(authService.getUserById(10)).thenReturn(user);
        when(dto.toTask(user)).thenReturn(task);
        when(repository.save(task)).thenReturn(task);

        Task created = taskService.createTask(dto);

        assertEquals(task, created);
        verify(repository).save(task);
    }

    @Test
    void updateTask_success() {
        UpdateTaskDTO dto = mock(UpdateTaskDTO.class);
        Task existing = new Task();
        existing.setId(1);
        User user = new User();

        when(dto.assignedTo()).thenReturn(10);
        when(dto.title()).thenReturn("Updated");
        when(dto.description()).thenReturn("Desc");
        when(dto.status()).thenReturn("DONE");

        when(authService.getUserById(10)).thenReturn(user);
        when(repository.findById(1)).thenReturn(Optional.of(existing));
        when(repository.save(existing)).thenReturn(existing);

        Task updated = taskService.updateTask(1, dto);

        assertEquals("Updated", updated.getTitle());
        assertEquals("Desc", updated.getDescription());
        assertEquals("DONE", updated.getStatus());
        assertEquals(user, updated.getAssignedTo());
    }

    @Test
    void deleteTask_success() {
        taskService.deleteTask(1);

        verify(repository).deleteById(1);
    }
}
