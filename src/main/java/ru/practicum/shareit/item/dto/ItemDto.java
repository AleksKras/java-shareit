package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import ru.practicum.shareit.user.User;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class ItemDto {
    private long id;
    private String name;
    private String description;
    @JsonProperty(value = "available")
    @NotNull
    private Boolean available;
    private User owner;
    private String request;
}
