package ru.practicum.shareit.booking;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

public interface BookingService {
    BookingDto create(BookingDto bookingDto, long userId);

    BookingDto update(BookingDto bookingDto, long userId);

    BookingDto approve(long bookingId, boolean isApprove, long userId);

    BookingDto getBooking(long id, long userId);


    List<BookingDto> getAllByUser(long userId, String state);

    List<BookingDto> getAllByUser(long userId, String state, Pageable pageable);

    List<BookingDto> getAllByOwner(long userId, String state, Pageable pageable);

    List<BookingDto> getAllByOwner(long userId, String state);

    void delete(long bookingId, long userId);


}
