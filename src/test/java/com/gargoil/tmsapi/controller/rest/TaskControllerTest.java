package com.gargoil.tmsapi.controller.rest;

import com.gargoil.tmsapi.controller.response.ResponseWrapper;
import com.gargoil.tmsapi.model.Task;
import com.gargoil.tmsapi.model.transporter.CreateTaskDTO;
import com.gargoil.tmsapi.model.transporter.UpdateTaskDTO;
import com.gargoil.tmsapi.service.TaskService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskControllerTest {

    @Mock
    private TaskService taskService;

    @InjectMocks
    private TaskController controller;

    @Test
    void createTask_success() {
        CreateTaskDTO dto = mock(CreateTaskDTO.class);
        Task task = new Task();
        task.setId(1);

        when(taskService.createTask(dto)).thenReturn(task);

        ResponseEntity<ResponseWrapper> response = controller.createTask(dto);

        assertEquals(201, response.getStatusCode().value());
        verify(taskService).createTask(dto);
    }

    @Test
    void getTask_success() {
        Task task = new Task();
        task.setId(1);

        when(taskService.getTaskById(1)).thenReturn(task);

        ResponseEntity<ResponseWrapper> response = controller.getTask(1);

        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    void updateTask_success() {
        UpdateTaskDTO dto = mock(UpdateTaskDTO.class);
        Task task = new Task();
        task.setId(1);

        when(taskService.updateTask(1, dto)).thenReturn(task);

        ResponseEntity<ResponseWrapper> response = controller.updateTask(1, dto);

        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    void deleteTask_success() {
        ResponseEntity<ResponseWrapper> response = controller.deleteTask(1);

        assertEquals(204, response.getStatusCode().value());
        verify(taskService).deleteTask(1);
    }
}
