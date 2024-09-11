package com.example.project.service;

import com.example.project.error_handling.exception.UserNotFoundException;
import com.example.project.model.User;
import com.example.project.repository.UserRepository;
import com.example.project.security.CustomUserDetails;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UserNotFoundException( "User not found with username: " + username);
        }
        // .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

       // System.out.println("Fetched user: " + user.getUsername() + ", " + user.getPassword());
        // Convert the user role to a GrantedAuthority
        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + user.getRole());
        return new CustomUserDetails(user, Collections.singleton(authority));
//        return org.springframework.security.core.userdetails.User.withUsername(user.getUsername())
//                .password(user.getPassword())
//                .authorities(Collections.singleton(authority))
//                .accountExpired(false)
//                .accountLocked(false)
//                .credentialsExpired(false)
//                //.disabled(!user.getUserStatus().toString().equals("ACTIVATED")) // Disable user if they are not 'ACTIVATED'
//                .disabled(false)
//                .build();


    }
}
