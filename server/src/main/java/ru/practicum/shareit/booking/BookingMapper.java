package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;

public interface BookingMapper {
    Booking toBooking(BookingDto bookingDto);

    BookingDto toDto(Booking booking);

    BookingShortDto toShortDto(Booking booking);

    void updateBookingFromDto(BookingDto bookingDto, Booking booking);
}
