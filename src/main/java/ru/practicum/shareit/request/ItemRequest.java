package ru.practicum.shareit.request;

import lombok.Data;
import ru.practicum.shareit.user.User;

import javax.validation.constraints.NotBlank;
import java.time.LocalDate;

@Data
public class ItemRequest {
    private long id;
    @NotBlank
    private String description;
    @NotBlank
    private User requestor;
    private LocalDate created;
}
