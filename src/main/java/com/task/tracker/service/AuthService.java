package com.task.tracker.service;


import com.task.tracker.dto.response.AuthenticationResponse;
import com.task.tracker.dto.request.SignIn;
import com.task.tracker.dto.request.SignUp;
import com.task.tracker.exception.EmptyException;
import com.task.tracker.exception.ExistsException;
import com.task.tracker.model.User;
import com.task.tracker.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;

    @Lazy
    private final UserService userService;

    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;


    public AuthenticationResponse signIn(SignIn signIn) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(signIn.getEmail(), signIn.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwtToken = jwtUtil.generateToken(authentication);
        log.info("User: " + signIn.getEmail() + " is login");
        return new AuthenticationResponse(jwtToken);
    }

    public void signUp(SignUp signUp) throws IOException {

        if (signUp.getEmail().isEmpty() || signUp.getPassword().isEmpty())
            throw new EmptyException("user");

        if (userService.existsByEmail(signUp.getEmail()))
            throw new ExistsException("email");

        User user = new User();
        user.setUsername(signUp.getUsername());
        user.setEmail(signUp.getEmail());
        user.setPassword(passwordEncoder.encode(signUp.getPassword()));

        userService.add(user);
    }
}