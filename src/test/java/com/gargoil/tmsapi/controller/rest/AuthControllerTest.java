package com.gargoil.tmsapi.controller.rest;

import com.gargoil.tmsapi.controller.response.ResponseWrapper;
import com.gargoil.tmsapi.model.User;
import com.gargoil.tmsapi.model.transporter.LoginDTO;
import com.gargoil.tmsapi.model.transporter.RegisterDTO;
import com.gargoil.tmsapi.service.AuthService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController controller;

    @Test
    void login_success() {
        LoginDTO dto = new LoginDTO("john", "password");

        when(authService.authenticate(dto)).thenReturn("jwt-token");

        ResponseEntity<ResponseWrapper> response = controller.login(dto);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("Bearer jwt-token", response.getHeaders().getFirst("Authorization"));
    }

    @Test
    void register_success() {
        RegisterDTO dto = mock(RegisterDTO.class);
        User user = new User();
        user.setId(1);

        when(authService.register(dto)).thenReturn(user);

        ResponseEntity<ResponseWrapper> response = controller.register(dto);

        assertEquals(200, response.getStatusCode().value());
        verify(authService).register(dto);
    }
}
