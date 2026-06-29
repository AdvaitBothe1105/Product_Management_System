package com.jforce.interview2.controller;
import com.jforce.interview2.dto.LoginRequest;
import com.jforce.interview2.dto.LoginResponse;
import com.jforce.interview2.dto.RegisterRequest;
import com.jforce.interview2.model.User;
import com.jforce.interview2.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public User register(@RequestBody RegisterRequest registerRequest) {
        return authService.register(registerRequest);
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest loginRequestDTO){
        return authService.login(loginRequestDTO);
    }

}
