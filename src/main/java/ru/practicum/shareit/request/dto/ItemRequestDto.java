package ru.practicum.shareit.request.dto;

import lombok.Data;
import ru.practicum.shareit.user.User;

import java.time.LocalDate;

@Data
public class ItemRequestDto {
    private long id;
    private String description;
    private User requestor;
    private LocalDate created;
}
