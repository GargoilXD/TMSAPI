package com.gargoil.tmsapi.model.transporter;

import com.gargoil.tmsapi.model.Task;
import com.gargoil.tmsapi.model.User;
import jakarta.validation.ValidationException;
import jakarta.validation.constraints.NotBlank;

public record CreateTaskDTO(
        @NotBlank
        String title,
        String description,
        @NotBlank
        String status,
        @NotBlank
        Integer assignedTo
) {
    public CreateTaskDTO {
        title = title.trim();
        description = description != null ? description.trim() : null;
        status = status.trim().toUpperCase();
        switch (status) {
            case "TODO", "IN_PROGRESS", "DONE" -> {}
            default -> throw new ValidationException("Invalid status");
        }
    }
    public Task toTask(User user) {
        return new Task(null, title, description, status, user);
    }
}
