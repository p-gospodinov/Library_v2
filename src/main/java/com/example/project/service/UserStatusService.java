package com.example.project.service;

import com.example.project.enums.UserStatus;
import com.example.project.model.User;
import com.example.project.repository.UserRepository;
import com.example.project.security.SecurityUtils;
import org.springframework.stereotype.Service;

@Service
public class UserStatusService {
    private final UserRepository userRepository;

    public UserStatusService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean isCurrentUserActive() {
        String username = SecurityUtils.getCurrentUsername();
        if (username == null) {
            throw new IllegalStateException("No authenticated user found.");
        }

        User user = userRepository.findByUsername(username);
        return user != null && user.getUserStatus() == UserStatus.ACTIVATED;
    }
}
