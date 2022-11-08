package ru.practicum.shareit.user;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Entity
@Table(name = "users")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank
    @Column(name = "NAME", nullable = false)
    private String name;
    @NotBlank
    @Email
    @Column(name = "EMAIL", nullable = false, length = 50)
    private String email;
}
