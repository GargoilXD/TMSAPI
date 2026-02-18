package com.gargoil.tmsapi.service;

import com.gargoil.tmsapi.exception.AuthenticationException;
import com.gargoil.tmsapi.model.User;
import com.gargoil.tmsapi.model.transporter.LoginDTO;
import com.gargoil.tmsapi.model.transporter.RegisterDTO;
import com.gargoil.tmsapi.repository.UserRepository;
import com.gargoil.tmsapi.security.JWT;
import com.gargoil.tmsapi.utility.PasswordHasher;
import jakarta.persistence.EntityExistsException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository repository;

    @Mock
    private PasswordHasher hasher;

    @Mock
    private JWT jwt;

    @InjectMocks
    private AuthService authService;

    @Test
    void authenticate_success() {
        LoginDTO dto = new LoginDTO("john", "password");
        User user = new User();
        user.setPassword("hashed");

        when(repository.findByUsername("john")).thenReturn(Optional.of(user));
        when(hasher.verify("password", "hashed")).thenReturn(true);
        when(jwt.generateToken(user)).thenReturn("jwt-token");

        String token = authService.authenticate(dto);

        assertEquals("jwt-token", token);
        verify(jwt).generateToken(user);
    }

    @Test
    void authenticate_invalidPassword_throwsException() {
        LoginDTO dto = new LoginDTO("john", "wrong");

        User user = new User();
        user.setPassword("hashed");

        when(repository.findByUsername("john")).thenReturn(Optional.of(user));
        when(hasher.verify("wrong", "hashed")).thenReturn(false);

        assertThrows(AuthenticationException.class, () -> authService.authenticate(dto));
    }

    @Test
    void authenticate_userNotFound_throwsException() {
        LoginDTO dto = new LoginDTO("unknown", "password");

        when(repository.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThrows(AuthenticationException.class, () -> authService.authenticate(dto));
    }

    @Test
    void register_success() {
        RegisterDTO dto = mock(RegisterDTO.class);
        User user = new User();

        when(dto.username()).thenReturn("john");
        when(dto.password()).thenReturn("password");
        when(hasher.hash("password")).thenReturn("hashed");
        when(dto.toUser("hashed")).thenReturn(user);
        when(repository.findByUsername("john")).thenReturn(Optional.empty());
        when(repository.save(user)).thenReturn(user);

        User saved = authService.register(dto);

        assertEquals(user, saved);
        verify(repository).save(user);
    }

    @Test
    void register_usernameExists_throwsException() {
        RegisterDTO dto = mock(RegisterDTO.class);

        when(dto.username()).thenReturn("john");
        when(repository.findByUsername("john")).thenReturn(Optional.of(new User()));

        assertThrows(EntityExistsException.class, () -> authService.register(dto));
    }
}
