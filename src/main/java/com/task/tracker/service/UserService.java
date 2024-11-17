package com.task.tracker.service;

import com.task.tracker.model.User;
import com.task.tracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public void add(User user) {
        log.info("Add user");
        userRepository.save(user);
    }

    public Optional<User> getUserById(Long id) {
        log.info("Get user by id");
        return userRepository.findById(id);
    }

    public Optional<User> getUserByEmail(String email) {
        log.info("Get user by email");
        return userRepository.findByEmail(email);
    }

    public Boolean existsByEmail(String email) {
        log.info("Exists user by email");
        return userRepository.existsByEmail(email);
    }

    public Boolean isAdminByEmail(String email) {
        User user = getUserByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return user.getRoles()
                .stream()
                .anyMatch(role -> role.getName().equals("ADMIN"));
    }
}
