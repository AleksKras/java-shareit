package ru.practicum.shareit.request;

import lombok.Data;
import ru.practicum.shareit.user.User;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Entity
@Table(name = "requests", schema = "public")
@Data
public class ItemRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank
    @Column(name = "DESCRIPTION", nullable = false)
    private String description;
    @NotBlank
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "REQUESTOR_ID")
    private User requestor;
}
