package com.example.project.service;

import com.example.project.enums.Role;
import com.example.project.enums.UserStatus;
import com.example.project.error_handling.exception.*;
import com.example.project.model.User;
import com.example.project.repository.UserRepository;
import com.example.project.security.CustomUserDetails;
import com.example.project.security.SecurityUtils;
import jakarta.mail.MessagingException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;


import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final EmailService emailService;

    private final UserStatusService userStatusService;
    public UserService(UserRepository userRepository, EmailService emailService, UserStatusService userStatusService) {
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.userStatusService = userStatusService;
    }

    public List<User> getAllUsers() {
        if (!userStatusService.isCurrentUserActive()) {
            throw new InactiveUserException("User is not active and cannot perform this action.");
        }

        List<User> users = userRepository.findAll();
        if (users == null || users.isEmpty()) {
            throw new UserNotFoundException("No users found.");
        }
        return users;
    }

    public User getUserById(int id) {
        if (!userStatusService.isCurrentUserActive()) {
            throw new InactiveUserException("User is not active and cannot perform this action.");
        }

        User user = userRepository.findById(id);
        if (user == null) {
            throw new UserNotFoundException("User with ID " + id + " not found.");
        }

        return user;
    }

    public Integer getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new InvalidLoginException("User is not authenticated");
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof CustomUserDetails) {
//            String username = ((UserDetails) principal).getUsername();
//            // Assuming you have a method to fetch the user by username
//            User user = userRepository.findByUsername(username);
//            return user.getUserID();
            return ((CustomUserDetails) principal).getUserId();
        } else {
            throw new UserNotFoundException("User details not found");
        }
    }

    public void createUser(User user) {
        user.setUserStatus(UserStatus.ACTIVATED);
        if (!userStatusService.isCurrentUserActive()) {
            throw new InactiveUserException("User is not active and cannot perform this action.");
        }
        try{
        userRepository.insert(user);
        } catch (Exception e) {
            throw new UserOperationException("Failed to create user", e);
        }
        String htmlBody = emailService.generateHtmlContentCreateUser(user);
        try {
            emailService.sendHtmlMessage("petargospodinov06@gmail.com", "Account created", htmlBody);
        } catch (MessagingException e) {
            throw new EmailNotSentException("Failed to send account creation email to user: " + user.getUsername(),e);
        }
    }

    public void updateUser(User user) {
        if (!userStatusService.isCurrentUserActive()) {
            throw new InactiveUserException("User is not active and cannot perform this action.");
        }
        try{
        userRepository.update(user);
        } catch (Exception e) {
            // Throw custom exception for any other failure
            throw new UserOperationException("Failed to update user details", e);
        }
    }

    public void deleteUser(int id) {
        if (!userStatusService.isCurrentUserActive()) {
            throw new InactiveUserException("User is not active and cannot perform this action.");
        }
        User user = userRepository.findById(id);
        if (user == null) {
            throw new UserNotFoundException("User with ID " + id + " not found.");
        }
        try{
        userRepository.deleteById(id);
        } catch (Exception e) {
            // Throw custom exception for any other failure
            throw new UserOperationException("Failed to delete the user with ID " + id, e);
        }
    }

//    public boolean isUserActive(int userId) {
//        User user = userRepository.findById(userId);
//        return user != null && user.getUserStatus() == UserStatus.ACTIVATED;
//    }

    public void banAccount(int id) {
        if (!userStatusService.isCurrentUserActive()) {
            throw new InactiveUserException("User is not active and cannot perform this action.");
        }

        User user = userRepository.findById(id);
        if (user == null) {
            throw new UserNotFoundException("User with ID " + id + " not found.");
        }
        if (user.getRole() == Role.ADMIN) {
            throw new AdminUserSuspensionException("Cannot ban an admin user.");
        }
        try {
            user.setUserStatus(UserStatus.BANNED);
            userRepository.update(user);
        }catch(Exception e){
            throw new UserOperationException("Failed to ban the user with ID " + id, e);
        }
    }

    public void suspendAccount(int id, int suspensionDurationInMinutes) throws MessagingException {
        if (!userStatusService.isCurrentUserActive()) {
            throw new InactiveUserException("User is not active and cannot perform this action.");
        }

        User user = userRepository.findById(id);

        if (user == null) {
            throw new UserNotFoundException("User with ID " + id + " not found.");
        }

        if (user.getRole() == Role.ADMIN) {
            throw new AdminUserSuspensionException("Cannot suspend an admin user.");
        }
        user.setUserStatus(UserStatus.SUSPENDED);
        user.setSuspensionEndTime(LocalDateTime.now().plusMinutes(suspensionDurationInMinutes));
        userRepository.update(user);
        try{
        String htmlBody = emailService.generateHtmlContentSuspendUser(user);
        emailService.sendHtmlMessage("petargospodinov06@gmail.com", "Account suspension", htmlBody);
        } catch (Exception e) {
            // Throw custom exception for any other failure
            throw new EmailNotSentException("Failed to send suspension email to user.", e);
        }
    }

    @Scheduled(fixedRate = 60000)  // Runs every minute
    public void checkSuspensionStatus() throws MessagingException {
      //  System.out.println("Checking suspension status for all users...");
        Iterable<User> users = userRepository.findSuspendedUsers();
        for (User user : users) {
            //System.out.println("Checking user: " + user.getUsername() + " with status: " + user.getUserStatus());
            boolean wasSuspended = user.isCurrentlySuspended();
            if (!wasSuspended) {
//            if (user.isCurrentlySuspended()) {
//                userRepository.update(user);
//                if (user.getUserStatus() == UserStatus.ACTIVATED) {
                //System.out.println("User " + user.getUsername() + " is no longer suspended. Updating status...");
                userRepository.update(user);
                try{
                String htmlBody = emailService.generateHtmlContentActivateUser(user);
                emailService.sendHtmlMessage("petargospodinov06@gmail.com", "Account reactivation", htmlBody);
                } catch (Exception e) {
                    // Throw custom exception for any other failure
                    throw new EmailNotSentException("Failed to send account reactivation email to user.", e);
                }
            }
        }
    }

//    public boolean isCurrentUserActive() {
//        String username = SecurityUtils.getCurrentUsername();
//        if (username == null) {
//            throw new IllegalStateException("No authenticated user found.");
//        }
//
//        User user = userRepository.findByUsername(username);
//        return user != null && user.getUserStatus() == UserStatus.ACTIVATED;
//    }

//    public void someServiceMethod() {
//        // Check if the current user is active
//        if (!userStatusService.isCurrentUserActive()) {
//            throw new InactiveUserException("User is not active and cannot perform this action.");
//        }
//
//        //  }
//    }
}
