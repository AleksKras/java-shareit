package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BookingShortDto {
    private long id;
    private Long bookerId;

    public BookingShortDto() {
    }
}
