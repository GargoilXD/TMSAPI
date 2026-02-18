package com.gargoil.tmsapi.controller.rest;

import com.gargoil.tmsapi.controller.response.ResponseWrapper;
import com.gargoil.tmsapi.model.transporter.LoginDTO;
import com.gargoil.tmsapi.model.transporter.RegisterDTO;
import com.gargoil.tmsapi.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ResponseWrapper> login(@RequestBody LoginDTO dto) {
        String token = authService.authenticate(dto);
        log.info("Login successful for user with token {}", token);
        return ResponseEntity.ok().header("Authorization", "Bearer " + token).body(ResponseWrapper.success(HttpStatus.OK, "Login successful", Map.of("token", token)));
    }

    @PostMapping("/register")
    public ResponseEntity<ResponseWrapper> register(@RequestBody RegisterDTO dto) {
        log.info("Registration successful for user with username {}", dto.username());
        return ResponseEntity.ok().body(ResponseWrapper.success(HttpStatus.OK, "Registration successful", Map.of("userId", authService.register(dto).getId())));
    }
}