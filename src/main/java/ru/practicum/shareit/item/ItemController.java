package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserServiceImpl;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@AllArgsConstructor
public class ItemController {
    private final ItemService itemService;
    private final UserServiceImpl userService;
    private final ItemMapper mapper;

    @PostMapping("/items")
    public ItemDto create(@Valid @RequestBody ItemDto itemDto,
                          @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Получен Post запрос к эндпоинту: /items");
        return mapper.toDto(itemService.create(itemDto, userId));
    }

    @PatchMapping("/items/{id}")
    public ItemDto update(@RequestBody ItemDto itemDto, @PathVariable("id") Integer id,
                          @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Получен Patch запрос к эндпоинту: /items. Обновление item:" + id);
        itemDto.setId(id);
        return mapper.toDto(itemService.update(itemDto, userId));
    }

    @GetMapping("/items/{id}")
    public ItemDto getItem(@PathVariable(required = true) Integer id) {
        return mapper.toDto(itemService.getItem(id));
    }

    @GetMapping("/items")
    public List<Item> getAll(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Получен Get запроск эндпоинту: /items");
        return itemService.getAll(userId);
    }

    @GetMapping("/items/search")
    public List<Item> search(@RequestHeader("X-Sharer-User-Id") long userId,
                             @RequestParam(value = "text", required = true) String query) {
        log.info("Получен Get запроск эндпоинту: /search");
        return itemService.search(query, userId);
    }

    @DeleteMapping("/items/{id}")
    public void delete(@PathVariable(required = true) Integer id,
                       @RequestHeader("X-Sharer-User-Id") long userId) {
        itemService.checkItemOwner(id, userId);
        itemService.delete(id);
    }

}
