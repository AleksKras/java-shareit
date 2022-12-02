package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingDTO;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping(path = "/items")
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDto create(@Valid @RequestBody ItemDto itemDto,
                          @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Получен Post запрос к эндпоинту: /items");
        return itemService.create(itemDto, userId);
    }

    @PostMapping("/{id}/comment")
    public CommentDto createComment(@Valid @RequestBody CommentDto commentDto,
                                    @PathVariable(required = true) long id,
                                    @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Получен Post запрос к эндпоинту: /items/comment");
        return itemService.createComment(commentDto, id, userId);
    }

    @PatchMapping("/{id}")
    public ItemDto update(@RequestBody ItemDto itemDto, @PathVariable("id") Integer id,
                          @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Получен Patch запрос к эндпоинту: /items. Обновление item:" + id);
        itemDto.setId(id);
        return itemService.update(itemDto, userId);
    }

    @GetMapping("/{id}")
    public ItemWithBookingDTO getItem(@PathVariable(required = true) long id,
                                      @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Получен Get запроск эндпоинту: /items. Запрос элемента с ID = " + id);
        return itemService.getItemWithBooking(id, userId);
    }

    @GetMapping
    public List<ItemWithBookingDTO> getAll(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Получен Get запроск эндпоинту: /items");
        return itemService.getAll(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestHeader("X-Sharer-User-Id") long userId,
                                @RequestParam(value = "text", required = true) String query) {
        log.info("Получен Get запроск эндпоинту: /search");
        return itemService.search(query, userId);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable(required = true) Integer id,
                       @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Получен Delete запрос к эндпоинту: /items. Удаление item:" + id);
        itemService.checkItemOwner(id, userId);
        itemService.delete(id);
    }

}
