package com.arboviroses.conectaDengue.Api.Controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.arboviroses.conectaDengue.Api.DTO.request.LoginUserDTO;
import com.arboviroses.conectaDengue.Api.DTO.request.RegisterUserDTO;
import com.arboviroses.conectaDengue.Api.DTO.response.LoginResponse;
import com.arboviroses.conectaDengue.Domain.Entities.Notification.User;
import com.arboviroses.conectaDengue.Domain.Services.AuthenticationService;
import com.arboviroses.conectaDengue.Domain.Services.JwtService;

@RequestMapping("api/auth")
@RestController
@CrossOrigin(origins = "http://localhost:5173")
public class AuthenticationController {
    private final JwtService jwtService;
    
    private final AuthenticationService authenticationService;

    public AuthenticationController(JwtService jwtService, AuthenticationService authenticationService) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
    }

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody RegisterUserDTO registerUserDto) {
        User registeredUser = authenticationService.signup(registerUserDto);

        return ResponseEntity.ok(registeredUser);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticate(@RequestBody LoginUserDTO loginUserDto) {        
        User authenticatedUser = authenticationService.authenticate(loginUserDto);

        String jwtToken = jwtService.generateToken(authenticatedUser);

        LoginResponse loginResponse = new LoginResponse(jwtToken, jwtService.getExpirationTime(), authenticatedUser.getFullName());

        return ResponseEntity.ok(loginResponse);
    }
}
