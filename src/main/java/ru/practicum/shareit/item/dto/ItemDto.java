package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.constraints.NotNull;

@Data
public class ItemDto {
    private long id;
    private String name;
    private String description;
    @JsonProperty(value = "available")
    @NotNull
    private Boolean available;
    private UserDto owner;
    private ItemRequest request;
}
