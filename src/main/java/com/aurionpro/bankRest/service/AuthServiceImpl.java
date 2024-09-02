package com.aurionpro.bankRest.service;

import com.aurionpro.bankRest.dto.LoginDto;
import com.aurionpro.bankRest.dto.RegistrationDto;
import com.aurionpro.bankRest.entity.Role;
import com.aurionpro.bankRest.entity.User;
import com.aurionpro.bankRest.exception.UserApiException;
import com.aurionpro.bankRest.repository.RoleRepository;
import com.aurionpro.bankRest.repository.UserRepository;
import com.aurionpro.bankRest.security.JwtTokenProvider;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
@RequiredArgsConstructor
@Data
public class AuthServiceImpl implements AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Override
    public User register(RegistrationDto registrationDto) {
        // TODO Auto-generated method stub
        if (userRepository.existsByUsername(registrationDto.getUsername()))
            throw new UserApiException(HttpStatus.BAD_REQUEST, "User already exists");

        User user = new User();
        user.setUsername(registrationDto.getUsername());
        user.setPassword(passwordEncoder.encode(registrationDto.getPassword()));

        List<Role> roles = new ArrayList<Role>();

        Role userRole = roleRepository.findByRoleName(registrationDto.getRole()).get();
        roles.add(userRole);
        user.setRoles(roles);
        return userRepository.save(user);
    }

    @Override
    public String login(LoginDto loginDto) {
        // TODO Auto-generated method stub
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            return jwtTokenProvider.generateToken(authentication);
        } catch (BadCredentialsException e) {
            // TODO: handle exception
            throw new UserApiException(HttpStatus.NOT_FOUND, "Username or Password is incorrect");
        }
    }

}
