package com.aurionpro.bankRest.controller;

import com.aurionpro.bankRest.dto.JwtAuthResponse;
import com.aurionpro.bankRest.dto.LoginDto;
import com.aurionpro.bankRest.dto.RegistrationDto;
import com.aurionpro.bankRest.entity.User;
import com.aurionpro.bankRest.service.AuthService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/api")
public class AuthController {
    @Autowired
    private AuthService authService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/register")
    public ResponseEntity<User> register(@Valid @RequestBody RegistrationDto registrationDto) {
        //TODO: process POST request
        return ResponseEntity.ok(authService.register(registrationDto));
    }

    @GetMapping("/login")
    public ResponseEntity<JwtAuthResponse> login(@Valid @RequestBody LoginDto loginDto) {




        String token = authService.login(loginDto);
        JwtAuthResponse jwtAuthResponse = new JwtAuthResponse();
        jwtAuthResponse.setAccessToken(token);

        return ResponseEntity.ok(jwtAuthResponse);
    }

}
