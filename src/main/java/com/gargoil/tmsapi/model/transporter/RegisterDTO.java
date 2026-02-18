package com.gargoil.tmsapi.model.transporter;

import com.gargoil.tmsapi.model.User;
import jakarta.validation.ValidationException;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RegisterDTO(
        @NotBlank @Size(min = 3, max = 50)
        String username,
        @NotBlank @Size(min = 8, max = 50)
        String password,
        @NotBlank @Size(min = 3, max = 50)
        String fullName,
        @NotBlank @Size(min = 5, max = 50)
        String email,
        @NotBlank @Size(min = 1, max = 1)
        String gender,
        @NotNull
        Boolean admin
) {
    public RegisterDTO {
        username = username.trim();
        fullName = fullName.trim();
        email = email.trim();
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) throw new ValidationException("Invalid email");
        gender = gender.toUpperCase();
        gender = switch (gender) {
            case "M", "F", "O" -> gender;
            case "MALE", "FEMALE", "OTHER" -> gender.substring(0, 1);
            default -> throw new ValidationException("Invalid gender");
        };
    }
    public User toUser(String passwordHash) {
        return new User(null, username, passwordHash, fullName, email, gender, admin);
    }
}
