package com.scms.repository;

import com.scms.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

// Repository - handles database operations for User (login credentials)
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Used during login to fetch a user by their username
    Optional<User> findByUsername(String username);
}

