package com.scms.service;

import com.scms.entity.User;
import com.scms.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.List;

// Bridges our custom User entity with Spring Security's authentication system.
// Spring Security calls loadUserByUsername() internally whenever a login is attempted.
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        // Convert our User entity into Spring Security's expected UserDetails object
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(), // already encrypted, Spring Security compares hashes
                List.of(new SimpleGrantedAuthority(user.getRole()))
        );
    }
}

