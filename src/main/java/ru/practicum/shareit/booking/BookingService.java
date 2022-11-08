package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

public interface BookingService {
    Booking create(BookingDto bookingDto, long userId);

    Booking update(BookingDto bookingDto, long userId);

    Booking approve(long bookingId, boolean isApprove, long userId);

    Booking getBooking(long id, long userId);

    List<BookingDto> getAllByUser(long userId);

    List<BookingDto> getAllByUser(long userId, String state);

    List<BookingDto> getAllByOwner(long userId);

    List<BookingDto> getAllByOwner(long userId, String state);

    void delete(long bookingId, long userId);


}
