package com.gargoil.tmsapi.controller.rest;

import com.gargoil.tmsapi.controller.response.ResponseWrapper;
import com.gargoil.tmsapi.model.transporter.LoginDTO;
import com.gargoil.tmsapi.model.transporter.RegisterDTO;
import com.gargoil.tmsapi.service.AuthService;
import lombok.RequiredArgsConstructor;
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
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ResponseWrapper> login(@RequestBody LoginDTO dto) {
        String token = authService.authenticate(dto);
        return ResponseEntity.ok().header("Authorization", "Bearer " + token).body(ResponseWrapper.success(HttpStatus.OK, "Login successful", Map.of("token", token)));
    }

    @PostMapping("/register")
    public ResponseEntity<ResponseWrapper> register(@RequestBody RegisterDTO dto) {
        return ResponseEntity.ok().body(ResponseWrapper.success(HttpStatus.OK, "Registration successful", Map.of("userId", authService.register(dto).getId())));
    }
}