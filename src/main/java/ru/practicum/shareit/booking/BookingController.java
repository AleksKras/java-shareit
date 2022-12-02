package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exception.ValidationException;

import javax.validation.Valid;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingDto create(@Valid @RequestBody BookingDto bookingDto,
                             @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Получен Post запрос к эндпоинту: /bookings");
        return bookingService.create(bookingDto, userId);
    }

    @PatchMapping("/{id}")
    public BookingDto update(@RequestBody(required = false) BookingDto bookingDto, @PathVariable("id") Integer id,
                             @RequestHeader("X-Sharer-User-Id") long userId,
                             @RequestParam(value = "approved", required = false) Optional<Boolean> approved) {
        if (approved.isPresent()) {
            return bookingService.approve(id, approved.get(), userId);
        } else {
            log.info("Получен Patch запрос к эндпоинту: /bookings. Обновление booking:" + id);
            bookingDto.setId(id);
            return bookingService.update(bookingDto, userId);
        }
    }

    @GetMapping("/{id}")
    public BookingDto getBooking(@PathVariable(required = true) Integer id,
                                 @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Получен Get запроск эндпоинту: /bookings. Запрос элемента с ID = " + id);
        return bookingService.getBooking(id, userId);
    }

    @GetMapping
    public List<BookingDto> getAllByUser(@RequestHeader("X-Sharer-User-Id") long userId,
                                         @RequestParam(value = "state", required = false) Optional<String> state,
                                         @RequestParam(value = "from") Optional<Integer> from,
                                         @RequestParam(value = "size") Optional<Integer> size) {
        log.info("Получен Get запроск эндпоинту: /bookings");
        String bookingState;
        if (state.isPresent()) {
            bookingState = state.get();
        } else {
            bookingState = BookingState.ALL.toString();
        }
        if (from.isPresent() && size.isPresent()) {
            if (from.get() < 0 || size.get() < 0) {
                throw new ValidationException("Ошибка в параметрах запроса");
            }
            return bookingService.getAllByUser(userId, bookingState, PageRequest.of((from.get() + 1) % size.get(),
                    size.get(), Sort.by("id").descending()));
        } else {
            return bookingService.getAllByUser(userId, bookingState);
        }
    }

    @GetMapping("/owner")
    public List<BookingDto> getAllByOwner(@RequestHeader("X-Sharer-User-Id") long userId,
                                          @RequestParam(value = "state", required = false) Optional<String> state,
                                          @RequestParam(value = "from") Optional<Integer> from,
                                          @RequestParam(value = "size") Optional<Integer> size) {
        log.info("Получен Get запроск эндпоинту: /bookings/owner");
        String bookingState;
        if (state.isPresent()) {
            bookingState = state.get();
        } else {
            bookingState = BookingState.ALL.toString();
        }
        if (from.isPresent() && size.isPresent()) {
            return bookingService.getAllByOwner(userId, bookingState, PageRequest.of(from.get(), size.get(),
                    Sort.by("id").descending()));
        } else {
            return bookingService.getAllByOwner(userId, bookingState);
        }
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable(required = true) Integer id,
                       @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Получен Delete запрос к эндпоинту: /bookings. Удаление booking:" + id);
        bookingService.delete(id, userId);
    }

}
