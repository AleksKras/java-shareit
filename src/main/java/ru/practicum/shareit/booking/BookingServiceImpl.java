package ru.practicum.shareit.booking;


import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserService;

import javax.validation.Valid;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
@Validated
public class BookingServiceImpl implements BookingService {
    private final UserService userService;
    private final ItemService itemService;
    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final UserMapper userMapper;
    private final ItemMapper itemMapper;

    @Override
    public Booking create(BookingDto bookingDto, long userId) {
        User user = userService.getUser(userId);
        Item item = itemService.getItem(bookingDto.getItemId());
        if (user.equals(item.getOwner())) {
            throw new NotFoundException("Запрещено создвать бронирование на свой Item");
        }
        bookingDto.setBooker(userMapper.toDto(user));
        bookingDto.setItem(itemMapper.toDto(item));
        Booking booking = bookingMapper.toBooking(bookingDto);
        checkBooking(booking);
        return bookingRepository.save(booking);
    }

    @Override
    public Booking update(BookingDto bookingDto, long userId) {
        Booking booking = getBooking(bookingDto.getId(), userId);
        bookingMapper.updateBookingFromDto(bookingDto, booking);
        checkBooking(booking);
        return bookingRepository.save(booking);
    }

    @Override
    public Booking getBooking(long id, long userId) {
        Booking booking = bookingRepository.getReferenceById(id);
        checkBookingAccess(booking, userId);
        return booking;
    }

    @Override
    public List<BookingDto> getAllByUser(long userId) {
        return getAllByUser(userId, "ALL");
    }

    @Override
    public List<BookingDto> getAllByUser(long userId, String state) {
        User user = userService.getUser(userId);
        log.info("Поиск запросов по пользователю: " + user.toString());
        return listBookingToDto(filterBooking(bookingRepository.findByBookerEquals(user), getBookingState(state)));
    }

    @Override
    public List<BookingDto> getAllByOwner(long userId) {
        return getAllByOwner(userId, "ALL");
    }

    @Override
    public List<BookingDto> getAllByOwner(long userId, String state) {
        User user = userService.getUser(userId);
        log.info("Поиск запросов по пользователю: " + user.toString());
        return listBookingToDto(filterBooking(bookingRepository.findAllOwner(userId), getBookingState(state)));
    }

    @Override
    public void delete(long bookingId, long userId) {
        Booking booking = getBooking(bookingId, userId);
        checkBookingAccess(booking, userId);
        bookingRepository.delete(booking);
    }

    private void checkBooking(@Valid Booking booking) {
        if (!booking.getItem().getAvailable()) {
            throw new ValidationException("Item не доступен");
        }
        if (booking.getStart().isAfter(booking.getEnd())) {
            throw new ValidationException("Ошибка в датах бронирования");
        }
        if (!bookingRepository.findAllByDate(booking.getStart(), booking.getEnd()).isEmpty()) {
            throw new ValidationException("Ошибка в датах бронирования");
        }
    }

    public void checkBookingAccess(Booking booking, long userId) {
        User user = userService.getUser(userId);
        if (!user.equals(booking.getBooker()) && !user.equals(booking.getItem().getOwner())) {
            throw new NotFoundException("Пользователь с ID=" + userId + " не имеет права изменять бронирование");
        }
    }

    @Override
    public Booking approve(long bookingId, boolean isApprove, long userId) {
        Booking booking = getBooking(bookingId, userId);
        User owner = booking.getItem().getOwner();
        itemService.checkItemOwner(booking.getItem().getId(), userId);
        if (isApprove) {
            if (booking.getStatus() != BookingStatus.APPROVED) {
                booking.setStatus(BookingStatus.APPROVED);
            } else {
                throw new ValidationException("Бронирование уже подтверждено");
            }

        } else {
            if (booking.getStatus() != BookingStatus.REJECTED) {
                booking.setStatus(BookingStatus.REJECTED);
            } else {
                throw new ValidationException("Бронирование уже отклонено");
            }
        }
        return update(bookingMapper.toDto(booking), userId);

    }

    private List<BookingDto> listBookingToDto(List<Booking> listBooking) {
        List<BookingDto> listBookingDto = new ArrayList<>();
        for (Booking booking : listBooking) {
            listBookingDto.add(bookingMapper.toDto(booking));
        }
        return listBookingDto;
    }

    private List<Booking> filterBooking(List<Booking> listBooking, BookingState state) {
        List<Booking> listBookingResult;
        switch (state) {
            case ALL:
                listBookingResult = listBooking;
                break;
            case FUTURE:
                listBookingResult = listBooking.stream()
                        .filter(booking -> ((booking.getStatus() == BookingStatus.APPROVED)
                                || (booking.getStatus() == BookingStatus.WAITING))
                                && (booking.getStart().isAfter(LocalDateTime.now())))
                        .collect(Collectors.toList());
                break;
            case WAITING:
                listBookingResult = listBooking.stream()
                        .filter(booking -> ((booking.getStatus() == BookingStatus.WAITING)
                                && (booking.getStart().isAfter(LocalDateTime.now()))))
                        .collect(Collectors.toList());
                break;
            case REJECTED:
                listBookingResult = listBooking.stream()
                        .filter(booking -> (booking.getStatus() == BookingStatus.REJECTED))
                        .collect(Collectors.toList());
                break;
            case CURRENT:
                listBookingResult = listBooking.stream()
                        .filter(booking -> ((booking.getStatus() == BookingStatus.REJECTED)
                                && (booking.getStart().isBefore(LocalDateTime.now()))
                                && (booking.getEnd().isAfter(LocalDateTime.now()))))
                        .collect(Collectors.toList());
                break;
            case PAST:
                listBookingResult = listBooking.stream()
                        .filter(booking -> ((booking.getStatus() == BookingStatus.APPROVED)
                                && (booking.getStart().isBefore(LocalDateTime.now()))
                                && (booking.getEnd().isBefore(LocalDateTime.now()))))
                        .collect(Collectors.toList());
                break;
            default:
                throw new ValidationException("{\"error\": \"Unknown state: UNSUPPORTED_STATUS\"}");
        }
        return sortListBooking(listBookingResult);
    }

    private List<Booking> sortListBooking(List<Booking> listBooking) {
        return listBooking.stream().sorted((booking0, booking1) -> {
            Long id0 = booking0.getId();
            Long id1 = booking1.getId();
            int comp = id1.compareTo(id0);
            return comp;
        }).collect(Collectors.toList());
    }

    private BookingState getBookingState(String state) {
        BookingState bookingState;
        try {
            bookingState = BookingState.valueOf(state);
        } catch (IllegalArgumentException e) {
            bookingState = BookingState.UNSUPPORTED;
        }
        return bookingState;
    }

}

