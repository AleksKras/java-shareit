package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping()
    public ItemRequestDto create(@RequestBody ItemRequestDto requestDto,
                                 @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Получен Post запрос к эндпоинту: /requests");
        return itemRequestService.create(requestDto, userId);
    }

    @GetMapping("/{id}")
    public ItemRequestDto getRequest(@PathVariable(required = true) Integer id,
                                     @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Получен Get запроск эндпоинту: /requests. Запрос элемента с ID = " + id);
        return itemRequestService.getRequest(id, userId);
    }

    @GetMapping()
    public List<ItemRequestDto> getAllByUser(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Получен Get запроск эндпоинту: /requests");
        return itemRequestService.getAllByUser(userId);

    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAll(@RequestHeader("X-Sharer-User-Id") long userId,
                                       @RequestParam(value = "from") Optional<Integer> from,
                                       @RequestParam(value = "size") Optional<Integer> size) {
        log.info("Получен Get запроск эндпоинту: /requests");
        if (from.isPresent() && size.isPresent()) {
            return itemRequestService.getAll(userId, PageRequest.of(from.get(), size.get(), Sort.by("created").descending()));
        } else {
            return new ArrayList<ItemRequestDto>();
        }
    }


    @DeleteMapping("/{id}")
    public void delete(@PathVariable(required = true) Integer id,
                       @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Получен Delete запрос к эндпоинту: /requests. Удаление request:" + id);
        itemRequestService.delete(id, userId);
    }

}
