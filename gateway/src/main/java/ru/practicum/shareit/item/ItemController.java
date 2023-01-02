package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;

@Slf4j
@RestController
@AllArgsConstructor
@Validated
@RequestMapping(path = "/items")
public class ItemController {
    private final ItemClient itemClient;


    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody ItemDto itemDto,
                                         @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Получен Post запрос к эндпоинту: /items");
        return itemClient.create(itemDto, userId);
    }

    @PostMapping("/{id}/comment")
    public ResponseEntity<Object> createComment(@Valid @RequestBody CommentDto commentDto,
                                                @PathVariable(required = true) long id,
                                                @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Получен Post запрос к эндпоинту: /items/comment");
        return itemClient.createComment(commentDto, id, userId);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> update(@RequestBody ItemDto itemDto, @PathVariable("id") Long id,
                                         @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Получен Patch запрос к эндпоинту: /items. Обновление item:" + id);
        itemDto.setId(id);
        return itemClient.update(id, itemDto, userId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getItem(@PathVariable(required = true) long id,
                                          @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Получен Get запроск эндпоинту: /items. Запрос элемента с ID = " + id);
        return itemClient.getItem(id, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAll(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Получен Get запроск эндпоинту: /items");
        return itemClient.getAll(userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(@RequestHeader("X-Sharer-User-Id") long userId,
                                         @RequestParam(value = "text", required = true) String query) {
        log.info("Получен Get запроск эндпоинту: /search");
        return itemClient.getItems(query, userId);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable(required = true) Long id,
                                         @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Получен Delete запрос к эндпоинту: /items. Удаление item:" + id);
        return itemClient.deleteItem(id, userId);
    }

}
