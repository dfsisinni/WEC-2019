package com.project.app.controllers;

import com.project.app.models.entities.User;
import com.project.app.models.v1.request.V1LoginOrRegisterRequest;
import com.project.app.models.v1.response.V1JwtAuthenticationResponse;
import com.project.app.models.v1.response.V1Response;
import com.project.app.repositories.UserRepository;
import com.project.app.security.JwtTokenProvider;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@RestController
@RequestMapping("/api/auth")
public class V1AuthController {

    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    private final UserRepository userRepository;

    @Autowired
    public V1AuthController(AuthenticationManager authenticationManager, PasswordEncoder passwordEncoder, JwtTokenProvider tokenProvider, UserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
        this.userRepository = userRepository;
    }

    @PostMapping("/signin")
    public ResponseEntity<V1Response<V1JwtAuthenticationResponse>> authenticateUser(@NotNull @Valid @RequestBody V1LoginOrRegisterRequest loginRequest) {
        val authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        val jwt = tokenProvider.generateToken(authentication);
        return ResponseEntity.ok(V1Response.of(new V1JwtAuthenticationResponse(jwt)));
    }

    @PostMapping("/register")
    public ResponseEntity registerUser(@NotNull @Valid @RequestBody V1LoginOrRegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            return new ResponseEntity(V1Response.error("Username is already registered"), HttpStatus.BAD_REQUEST);
        }

        this.createUserFromRequestTransactional(request);
        return this.authenticateUser(request);
    }

    @Transactional
    protected void createUserFromRequestTransactional(V1LoginOrRegisterRequest request) {
        val user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);
    }

}
