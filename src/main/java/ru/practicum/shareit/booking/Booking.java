package ru.practicum.shareit.booking;

import lombok.Data;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
public class Booking {
    private long id;
    @NotBlank
    @Future
    private LocalDateTime start;
    @Future
    private LocalDateTime end;
    @NotBlank
    private Item item;
    @NotBlank
    private User booker;
    @NotBlank
    private BookingStatus status;
}
