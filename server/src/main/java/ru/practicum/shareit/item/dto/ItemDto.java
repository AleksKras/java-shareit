package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.dto.UserDto;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemDto {
    private long id;
    private String name;
    private String description;
    @JsonProperty(value = "available")
    private Boolean available;
    private UserDto owner;
    private Long requestId;
}
