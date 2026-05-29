package com.rubun.bloom_username_checker.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String email;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}

/*
What each annotation does
@Entity Tells Hibernate "map this class to a DB table"
@Table(name="users")Names the table explicitly
@Id + @GeneratedValueAuto-incrementing primary key
@Column(unique=true)DB-level unique constraint on username
@BuilderLets us do User.builder().username("x").build()
*/