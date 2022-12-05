package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.exception.ValidationException;
import javax.validation.Valid;
import java.util.Optional;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader("X-Sharer-User-Id") long userId,
                                         @RequestBody @Valid BookingDto bookingDto) {
        log.info("Creating booking {}, userId={}", bookingDto, userId);
        return bookingClient.create(userId, bookingDto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> update(@RequestBody(required = false) BookingDto bookingDto, @PathVariable("id") long id,
                                         @RequestHeader("X-Sharer-User-Id") long userId,
                                         @RequestParam(value = "approved", required = false) Optional<Boolean> approved) {
        if (approved.isPresent()) {
            return bookingClient.approve(id, userId, approved.get());
        } else {
            log.info("Получен Patch запрос к эндпоинту: /bookings. Обновление booking:" + id);
            bookingDto.setId(id);
            return bookingClient.update(bookingDto, userId);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getBooking(@PathVariable(required = true) long id,
                                             @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Получен Get запроск эндпоинту: /bookings. Запрос элемента с ID = " + id);
        return bookingClient.getBooking(id, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllByUser(@RequestHeader("X-Sharer-User-Id") long userId,
                                               @RequestParam(name = "state", defaultValue = "ALL") String stateParam,
                                               @RequestParam(value = "from") Optional<Integer> from,
                                               @RequestParam(value = "size") Optional<Integer> size) {
        log.info("Получен Get запроск эндпоинту: /bookings");
        BookingState bookingState = BookingState.from(stateParam);
        if (bookingState == BookingState.UNSUPPORTED) {
            throw new ValidationException("{\"error\": \"Unknown state: UNSUPPORTED_STATUS\"}");
        }
        if (from.isPresent() && size.isPresent()) {
            if (from.get() < 0 || size.get() < 0) {
                throw new IllegalArgumentException("Ошибка в параметрах запроса");
            }
            return bookingClient.getBookingsByUser(userId, bookingState, from.get(), size.get());
        } else {
            return bookingClient.getBookingsByUser(userId, bookingState);
        }
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getAllByOwner(@RequestHeader("X-Sharer-User-Id") long userId,
                                                @RequestParam(name = "state", defaultValue = "ALL") String stateParam,
                                                @RequestParam(value = "from") Optional<Integer> from,
                                                @RequestParam(value = "size") Optional<Integer> size) {
        log.info("Получен Get запроск эндпоинту: /bookings/owner");
        BookingState bookingState = BookingState.from(stateParam);
        if (bookingState == BookingState.UNSUPPORTED) {
            throw new ValidationException("{\"error\": \"Unknown state: UNSUPPORTED_STATUS\"}");
        }
        if (from.isPresent() && size.isPresent()) {
            if (from.get() < 0 || size.get() < 0) {
                throw new ValidationException("Ошибка в параметрах запроса");
            }
            return bookingClient.getBookingsByOwner(userId, bookingState, from.get(), size.get());
        } else {
            return bookingClient.getBookingsByOwner(userId, bookingState);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteBooking(@PathVariable(required = true) long id,
                                                @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Получен Delete запрос к эндпоинту: /bookings. Удаление booking:" + id);
        return bookingClient.deleteBooking(id, userId);
    }
}
