package com.jforce.interview2.service;

import com.jforce.interview2.dto.LoginRequest;
import com.jforce.interview2.dto.LoginResponse;
import com.jforce.interview2.dto.RegisterRequest;
import com.jforce.interview2.model.Role;
import com.jforce.interview2.model.User;
import com.jforce.interview2.repo.UserRepo;
import com.jforce.interview2.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;


    public User register(RegisterRequest registerRequest){
        if(userRepo.existsByEmail(registerRequest.email())) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User();
        user.setName(registerRequest.name());
        user.setEmail(registerRequest.email());
        user.setPassword(bCryptPasswordEncoder.encode(registerRequest.password()));
        user.setRole(Role.USER);

        return userRepo.save(user);

    }

    public LoginResponse login(LoginRequest loginRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.email(),
                        loginRequest.password()
                )
        );
        User user =userRepo.findByEmail(loginRequest.email())
                .orElseThrow(() -> new RuntimeException("User not found"));
        String token =jwtUtil.generateToken(user.getEmail());

        return new LoginResponse(
                token,
                user.getEmail(),
                user.getRole().name()
        );
    }
}