package com.aurionpro.bankRest.service;

import com.aurionpro.bankRest.dto.LoginDto;
import com.aurionpro.bankRest.dto.RegistrationDto;
import com.aurionpro.bankRest.entity.Customer;
import com.aurionpro.bankRest.entity.Role;
import com.aurionpro.bankRest.entity.User;
import com.aurionpro.bankRest.exception.UserApiException;
import com.aurionpro.bankRest.repository.CustomerRepository;
import com.aurionpro.bankRest.repository.RoleRepository;
import com.aurionpro.bankRest.repository.UserRepository;
import com.aurionpro.bankRest.security.JwtTokenProvider;
import com.aurionpro.bankRest.utils.EntityIsActiveChecker;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.util.stream.Collectors;

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

    @Autowired
    private CustomerRepository customerRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthServiceImpl.class);

    @Override
    public User register(RegistrationDto registrationDto) {
        LOGGER.info("Attempting to register user with username: {}", registrationDto.getUsername());

        // Check if the username already exists
        if (userRepository.existsByUsername(registrationDto.getUsername())) {
            LOGGER.error("Registration failed: Username '{}' already exists", registrationDto.getUsername());
            throw new UserApiException(HttpStatus.BAD_REQUEST, "User already exists");
        }

        // Create a new user entity and set the username and encoded password
        User user = new User();
        user.setUsername(registrationDto.getUsername());
        user.setPassword(passwordEncoder.encode(registrationDto.getPassword()));

        LOGGER.info("User entity created for username: {}. Password is encoded.", registrationDto.getUsername());

        // Assign roles to the user
        List<Role> roles = new ArrayList<>();
        Role userRole = roleRepository.findByRoleName(registrationDto.getRole())
                .orElseThrow(() -> {
                    LOGGER.error("Role '{}' not found for registration", registrationDto.getRole());
                    return new UserApiException(HttpStatus.BAD_REQUEST, "Invalid role");
                });
        roles.add(userRole);
        user.setRoles(roles);

        LOGGER.info("Roles assigned to user: {}", roles.stream().map(Role::getRoleName).collect(Collectors.joining(", ")));

        // Save the user entity and return
        User savedUser = userRepository.save(user);

        LOGGER.info("User registered successfully with username: {}", savedUser.getUsername());

        return savedUser;
    }


    @Override
    public String login(LoginDto loginDto) {
        // TODO Auto-generated method stub
        try {
            User user = userRepository.findByUsername(loginDto.getUsername()).orElseThrow(()->{
                return new UserApiException(HttpStatus.NOT_FOUND,"Username not found");
            });

            Customer customer = customerRepository.findByUser(user).orElseThrow(()-> {
                        return new UserApiException(HttpStatus.NOT_FOUND, "Username not found");
            });

            EntityIsActiveChecker.checkIfCustomerIsActive(customer);


            //jwt token
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            return jwtTokenProvider.generateToken(authentication);
        } catch (BadCredentialsException e) {
            // TODO: handle exception
            throw new UserApiException(HttpStatus.NOT_FOUND, "Username or Password is incorrect");
        }
    }
}
