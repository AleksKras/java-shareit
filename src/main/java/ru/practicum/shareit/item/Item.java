package ru.practicum.shareit.item;

import lombok.Data;
import ru.practicum.shareit.user.User;

import javax.validation.constraints.NotBlank;

@Data
public class Item {
    private long id;
    @NotBlank
    private String name;
    private String description;
    private boolean available;
    @NotBlank
    private User owner;
    private String request;
}
