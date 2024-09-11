package com.example.project.model;

import com.example.project.enums.BookStatus;
import com.example.project.enums.Role;
import com.example.project.enums.UserStatus;

import java.time.LocalDateTime;

public class User {
    private int userID;
    private String username;
    private String password;
    private Role role;
    private UserStatus userStatus;
    private LocalDateTime suspensionEndTime;

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public UserStatus getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(UserStatus userStatus) {
        this.userStatus = userStatus;
    }

    public LocalDateTime getSuspensionEndTime() {
        return suspensionEndTime;
    }

    public void setSuspensionEndTime(LocalDateTime suspensionEndTime) {
        this.suspensionEndTime = suspensionEndTime;
    }

    public boolean isCurrentlySuspended() {
        if (userStatus != UserStatus.SUSPENDED) {
            System.out.println("User is not suspended.");
            return false;
        }
        //isSuspended = true;
        if (suspensionEndTime != null && suspensionEndTime.isBefore(LocalDateTime.now())) {
            System.out.println("Suspension period ended. Reactivating user...");
            setUserStatus(UserStatus.ACTIVATED); // Reset status to active
            setSuspensionEndTime(null);
            return false;
        }
        System.out.println("User is still suspended.");
        return true;
    }
}
