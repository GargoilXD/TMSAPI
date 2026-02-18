package com.gargoil.tmsapi.service;

import com.gargoil.tmsapi.exception.AuthenticationException;
import com.gargoil.tmsapi.model.User;
import com.gargoil.tmsapi.model.transporter.LoginDTO;
import com.gargoil.tmsapi.model.transporter.RegisterDTO;
import com.gargoil.tmsapi.repository.UserRepository;
import com.gargoil.tmsapi.security.JWT;
import com.gargoil.tmsapi.utility.PasswordHasher;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository repository;
    private final PasswordHasher hasher;
    private final JWT jwt;

    public String authenticate(LoginDTO dto) {
        User user = repository.findByUsername(dto.username()).orElseThrow(() -> new AuthenticationException("Invalid username or password"));
        if (!hasher.verify(dto.password(), user.getPassword())) throw new AuthenticationException("Invalid username or password");
        return jwt.generateToken(user);
    }
    public User register(RegisterDTO dto) {
        repository.findByUsername(dto.username()).ifPresent(ignored -> { throw new EntityExistsException("Username '%s' already exists".formatted(dto.username())); });
        return repository.save(dto.toUser(hasher.hash(dto.password())));
    }
    public User getUserByUsername(String username) {
        return repository.findByUsername(username).orElseThrow(() -> new EntityNotFoundException("User not found"));
    }
}
