package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;

@Component
@Slf4j
@AllArgsConstructor
public class BookingMapperImpl implements BookingMapper {

    private final UserMapper userMapper;
    private final ItemMapper itemMapper;

    @Override
    public Booking toBooking(BookingDto bookingDto) {
        if (bookingDto == null) {
            return null;
        }
        log.info(bookingDto.toString());
        Booking booking = new Booking();
        booking.setId(bookingDto.getId());
        booking.setStart(bookingDto.getStart());
        booking.setEnd(bookingDto.getEnd());
        booking.setItem(itemMapper.toItem(bookingDto.getItem()));
        booking.setBooker(userMapper.toUser(bookingDto.getBooker()));
        booking.setStatus(bookingDto.getStatus());
        if (booking.getStatus() == null) {
            booking.setStatus(BookingStatus.WAITING);
        }
        return booking;
    }

    @Override
    public BookingDto toDto(Booking booking) {
        if (booking == null) {
            return null;
        }

        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(booking.getId());
        bookingDto.setStart(booking.getStart());
        bookingDto.setEnd(booking.getEnd());
        bookingDto.setItem(itemMapper.toDto(booking.getItem()));
        bookingDto.setBooker(userMapper.toDto(booking.getBooker()));
        bookingDto.setStatus(booking.getStatus());

        return bookingDto;
    }

    @Override
    public BookingShortDto toShortDto(Booking booking) {
        if (booking == null) {
            return null;
        }

        BookingShortDto bookingShortDto = new BookingShortDto();
        bookingShortDto.setId(booking.getId());
        bookingShortDto.setBookerId(booking.getBooker().getId());

        return bookingShortDto;
    }

    @Override
    public void updateBookingFromDto(BookingDto bookingDto, Booking booking) {
        if (bookingDto == null) {
            return;
        }

        booking.setId(bookingDto.getId());
        if (bookingDto.getStart() != null) {
            booking.setStart(bookingDto.getStart());
        }
        if (bookingDto.getEnd() != null) {
            booking.setEnd(bookingDto.getEnd());
        }
        if (bookingDto.getItem() != null) {
            if (booking.getItem() == null) {
                booking.setItem(new Item());
            }
            booking.setItem(itemMapper.toItem((bookingDto.getItem())));
        }
        if (bookingDto.getBooker() != null) {
            if (booking.getBooker() == null) {
                booking.setBooker(new User());
            }
            booking.setBooker(userMapper.toUser(bookingDto.getBooker()));
        }
        if (bookingDto.getStatus() != null) {
            booking.setStatus(bookingDto.getStatus());
        }
    }
}
