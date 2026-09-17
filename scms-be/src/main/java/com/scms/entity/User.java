package com.scms.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Entity class - maps to 'students' table in MySQL
@Entity
@Table(name = "users")
@Data // Lombok: auto-generates getters, setters, toString
@NoArgsConstructor // Lombok: generates empty constructor
@AllArgsConstructor // Lombok: generates constructor with all fields
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-increment ID
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String role; //user or Admin

}