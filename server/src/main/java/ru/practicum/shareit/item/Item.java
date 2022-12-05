package ru.practicum.shareit.item;

import lombok.Data;
import ru.practicum.shareit.user.User;

import javax.persistence.*;

@Entity
@Table(name = "items")
@Data
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "NAME", nullable = false)
    private String name;
    @Column(name = "DESCRIPTION", nullable = false, length = 250)
    private String description;
    @Column(name = "IS_AVAILABLE")
    private Boolean available;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "OWNER_ID")
    private User owner;
    @JoinColumn(name = "REQUEST_ID")
    private Long requestId;
}
