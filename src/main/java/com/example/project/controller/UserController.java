package com.example.project.controller;

import com.example.project.model.User;
import com.example.project.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/user")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "User Controller", description = "Operations related to user management")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }
    @GetMapping
    @Operation(summary = "Get all users", description = "Retrieve all users from the database")
    public List<User> getAllUsers(){
        return userService.getAllUsers();
    }

    @PostMapping
    @Operation(summary = "Create account", description = "Create account in the database")
    public ResponseEntity<String> createUser(@RequestBody User user){
        userService.createUser(user);
        return new ResponseEntity<>(user.getRole().toString() + " with ID: "+ user.getUserID()+" created successfully!", HttpStatus.CREATED);
    }

    @PutMapping
    @Operation(summary = "Update user information", description = "Update the information for a user in the database")
    public ResponseEntity<String> updateUser(@RequestBody User user){
        userService.updateUser(user);
        return new ResponseEntity<>(user.getUsername()+ " with ID: " +user.getUserID()+" has been modified successfully!", HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/ban-user/{id}")
    @Operation(summary = "Ban a user", description = "Change a user's status to banned")
    public ResponseEntity<String> banUser(@PathVariable int id){
        userService.banAccount(id);
        return new ResponseEntity<>("User with ID: " +id+" has been banned!", HttpStatus.OK);
    }

    @PutMapping("/suspend-user/{userId}")
    @PreAuthorize("hasRole('ADMIN')") // Only admins can access this endpoint
    @Operation(summary = "Suspend a user", description = "Change a user's status to suspended")
    public ResponseEntity<String> suspendUser(@PathVariable int userId, @RequestParam int durationInMinutes) throws MessagingException {
         userService.suspendAccount(userId, durationInMinutes);
        return new ResponseEntity<>("User with ID: " +userId+" has been suspended!", HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a user", description = "Delete user's account from the database")
    public ResponseEntity<String> deleteUser(@PathVariable int id){
        userService.deleteUser(id);
        return new ResponseEntity<>("User with ID: " +id+" has been deleted successfully!", HttpStatus.OK);
    }

//    @PostMapping("/perform-action")
//    public ResponseEntity<String> performAction() {
//        // Call the service method; it internally checks if the user is active
//        userService.someServiceMethod();
//        return new ResponseEntity<>("Action performed successfully!", HttpStatus.OK);
//    }
}
