package com.aurionpro.bankRest.service;

import com.aurionpro.bankRest.dto.LoginDto;
import com.aurionpro.bankRest.dto.RegistrationDto;
import com.aurionpro.bankRest.entity.User;

public interface AuthService {
    User register(RegistrationDto registrationDto);

    String login(LoginDto loginDto);
}
