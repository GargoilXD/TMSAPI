package com.gargoil.tmsapi.model.transporter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginDTO(
        @NotBlank @Size(min = 3, max = 50)
        String username,
        @NotBlank @Size(min = 8, max = 50)
        String password
) {
    public LoginDTO {
        username = username.trim();
    }
}
