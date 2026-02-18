package com.gargoil.tmsapi.model.transporter;

import com.gargoil.tmsapi.model.Task;
import jakarta.validation.constraints.NotBlank;

public record TaskResponseDTO(
        @NotBlank
        String title,
        String description,
        @NotBlank
        String status,
        Integer assignedTo
) {
    public TaskResponseDTO(Task task) {
        this(task.getTitle(), task.getDescription(), task.getStatus(), (task.getAssignedTo() != null) ? task.getAssignedTo().getId() : null);
    }
}
