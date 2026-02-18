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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordHasher passwordHasher;

    @Mock
    private JWT jwt;

    @InjectMocks
    private AuthService authService;

    private User testUser;
    private LoginDTO loginDTO;
    private RegisterDTO registerDTO;

    @BeforeEach
    void setUp() {
        testUser = new User(1, "testuser", "hashedPassword", "Test User", "test@example.com", "M", false);
        loginDTO = new LoginDTO("testuser", "password123");
        registerDTO = new RegisterDTO("newuser", "password123", "Test User", "test@example.com", "M", false);
    }

    // Authenticate Tests
    @Test
    void testAuthenticateSuccess() {
        String token = "test.jwt.token";
        when(userRepository.findByUsername(loginDTO.username())).thenReturn(Optional.of(testUser));
        when(passwordHasher.verify("password123", testUser.getPassword())).thenReturn(true);
        when(jwt.generateToken(testUser)).thenReturn(token);

        String result = authService.authenticate(loginDTO);

        assertEquals(token, result);
        verify(userRepository, times(1)).findByUsername(loginDTO.username());
        verify(passwordHasher, times(1)).verify(loginDTO.password(), testUser.getPassword());
        verify(jwt, times(1)).generateToken(testUser);
    }

    @Test
    void testAuthenticateWithInvalidUsername() {
        when(userRepository.findByUsername(loginDTO.username())).thenReturn(Optional.empty());

        assertThrows(AuthenticationException.class, () -> authService.authenticate(loginDTO));
        verify(userRepository, times(1)).findByUsername(loginDTO.username());
        verify(passwordHasher, never()).verify(anyString(), anyString());
        verify(jwt, never()).generateToken(any());
    }

    @Test
    void testAuthenticateWithInvalidPassword() {
        when(userRepository.findByUsername(loginDTO.username())).thenReturn(Optional.of(testUser));
        when(passwordHasher.verify("password123", testUser.getPassword())).thenReturn(false);

        assertThrows(AuthenticationException.class, () -> authService.authenticate(loginDTO));
        verify(userRepository, times(1)).findByUsername(loginDTO.username());
        verify(passwordHasher, times(1)).verify(loginDTO.password(), testUser.getPassword());
        verify(jwt, never()).generateToken(any());
    }

    // Register Tests
    @Test
    void testRegisterSuccess() {
        String hashedPassword = "hashedNewPassword";
        User newUser = new User(null, registerDTO.username(), hashedPassword, registerDTO.fullName(),
                registerDTO.email(), registerDTO.gender(), registerDTO.admin());
        User savedUser = new User(2, registerDTO.username(), hashedPassword, registerDTO.fullName(),
                registerDTO.email(), registerDTO.gender(), registerDTO.admin());

        when(userRepository.findByUsername(registerDTO.username())).thenReturn(Optional.empty());
        when(passwordHasher.hash(registerDTO.password())).thenReturn(hashedPassword);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        User result = authService.register(registerDTO);

        assertNotNull(result);
        assertEquals(registerDTO.username(), result.getUsername());
        assertEquals(registerDTO.fullName(), result.getFullName());
        assertEquals(registerDTO.email(), result.getEmail());
        verify(userRepository, times(1)).findByUsername(registerDTO.username());
        verify(passwordHasher, times(1)).hash(registerDTO.password());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testRegisterWithExistingUsername() {
        when(userRepository.findByUsername(registerDTO.username())).thenReturn(Optional.of(testUser));

        assertThrows(EntityExistsException.class, () -> authService.register(registerDTO));
        verify(userRepository, times(1)).findByUsername(registerDTO.username());
        verify(passwordHasher, never()).hash(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testRegisterSavesUserWithHashedPassword() {
        String hashedPassword = "hashedPassword123";
        User savedUser = new User(3, registerDTO.username(), hashedPassword, registerDTO.fullName(),
                registerDTO.email(), registerDTO.gender(), registerDTO.admin());

        when(userRepository.findByUsername(registerDTO.username())).thenReturn(Optional.empty());
        when(passwordHasher.hash(registerDTO.password())).thenReturn(hashedPassword);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        authService.register(registerDTO);

        verify(userRepository).save(argThat(user ->
                user.getUsername().equals(registerDTO.username()) &&
                user.getPassword().equals(hashedPassword) &&
                user.getFullName().equals(registerDTO.fullName())
        ));
    }

    // GetUserByUsername Tests
    @Test
    void testGetUserByUsernameSuccess() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        User result = authService.getUserByUsername("testuser");

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals("Test User", result.getFullName());
        verify(userRepository, times(1)).findByUsername("testuser");
    }

    @Test
    void testGetUserByUsernameNotFound() {
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> authService.getUserByUsername("nonexistent"));
        verify(userRepository, times(1)).findByUsername("nonexistent");
    }

    @Test
    void testGetUserByUsernameWithNullUsername() {
        when(userRepository.findByUsername(null)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> authService.getUserByUsername(null));
        verify(userRepository, times(1)).findByUsername(null);
    }

    @Test
    void testGetUserByUsernameReturnsCorrectUser() {
        User differentUser = new User(5, "anotheruser", "password", "Another User", "another@example.com", "F", true);
        when(userRepository.findByUsername("anotheruser")).thenReturn(Optional.of(differentUser));

        User result = authService.getUserByUsername("anotheruser");

        assertEquals(5, result.getId());
        assertEquals("anotheruser", result.getUsername());
        assertEquals("Another User", result.getFullName());
        assertTrue(result.getAdmin());
    }
}
