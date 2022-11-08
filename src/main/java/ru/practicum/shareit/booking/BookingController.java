package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.user.UserServiceImpl;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;
    private final UserServiceImpl userService;
    private final BookingMapper mapper;

    @PostMapping
    public BookingDto create(@Valid @RequestBody BookingDto bookingDto,
                             @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Получен Post запрос к эндпоинту: /bookings");
        return mapper.toDto(bookingService.create(bookingDto, userId));
    }

    @PatchMapping("/{id}")
    public BookingDto update(@RequestBody(required = false) BookingDto bookingDto, @PathVariable("id") Integer id,
                             @RequestHeader("X-Sharer-User-Id") long userId,
                             @RequestParam(value = "approved", required = false) Optional<Boolean> approved) {
        if (approved.isPresent()) {
            return mapper.toDto(bookingService.approve(id, approved.get(), userId));
        } else {
            log.info("Получен Patch запрос к эндпоинту: /bookings. Обновление booking:" + id);
            bookingDto.setId(id);
            return mapper.toDto(bookingService.update(bookingDto, userId));
        }
    }

    @GetMapping("/{id}")
    public BookingDto getBooking(@PathVariable(required = true) Integer id,
                                 @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Получен Get запроск эндпоинту: /bookings. Запрос элемента с ID = " + id);
        return mapper.toDto(bookingService.getBooking(id, userId));
    }

    @GetMapping
    public List<BookingDto> getAllByUser(@RequestHeader("X-Sharer-User-Id") long userId,
                                         @RequestParam(value = "state", required = false) Optional<String> state) {
        log.info("Получен Get запроск эндпоинту: /bookings");
        if (state.isPresent()) {
            return bookingService.getAllByUser(userId, state.get());
        } else {
            return bookingService.getAllByUser(userId);
        }
    }

    @GetMapping("/owner")
    public List<BookingDto> getAllByOwner(@RequestHeader("X-Sharer-User-Id") long userId,
                                          @RequestParam(value = "state", required = false) Optional<String> state) {
        log.info("Получен Get запроск эндпоинту: /bookings/owner");
        if (state.isPresent()) {
            return bookingService.getAllByOwner(userId, state.get());
        } else {
            return bookingService.getAllByOwner(userId);
        }
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable(required = true) Integer id,
                       @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Получен Delete запрос к эндпоинту: /bookings. Удаление booking:" + id);
        bookingService.delete(id, userId);
    }

}
