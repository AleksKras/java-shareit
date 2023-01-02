package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import java.util.Optional;


@Slf4j
@RestController
@AllArgsConstructor
@Validated
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestBody @Valid ItemRequestDto requestDto,
                                         @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Получен Post запрос к эндпоинту: /requests");
        return itemRequestClient.create(requestDto, userId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getRequest(@PathVariable(required = true) long id,
                                             @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Получен Get запроск эндпоинту: /requests. Запрос элемента с ID = " + id);
        return itemRequestClient.getRequest(id, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllByUser(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Получен Get запроск эндпоинту: /requests");
        return itemRequestClient.getAllByUser(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAll(@RequestHeader("X-Sharer-User-Id") long userId,
                                         @RequestParam(value = "from") Optional<Integer> from,
                                         @RequestParam(value = "size") Optional<Integer> size) {
        log.info("Получен Get запроск эндпоинту: /requests");
        if (from.isPresent() && size.isPresent()) {
            return itemRequestClient.getAll(userId, from.get(), size.get());
        } else {
            return itemRequestClient.getAll(userId);
        }
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable(required = true) long id,
                       @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Получен Delete запрос к эндпоинту: /requests. Удаление request:" + id);
        itemRequestClient.deleteRequest(id, userId);
    }

}
