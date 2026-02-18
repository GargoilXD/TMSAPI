package com.gargoil.tmsapi.model.transporter;

import jakarta.validation.ValidationException;
import jakarta.validation.constraints.NotBlank;

public record UpdateTaskDTO(
        @NotBlank
        String title,
        @NotBlank
        String description,
        @NotBlank
        String status,
        @NotBlank
        Integer assignedTo
) {
    public UpdateTaskDTO {
        title = title.trim();
        description = description.trim();
        status = status.trim().toUpperCase();
        switch (status) {
            case "TODO", "IN_PROGRESS", "DONE" -> {}
            default -> throw new ValidationException("Invalid status");
        }
    }
}
