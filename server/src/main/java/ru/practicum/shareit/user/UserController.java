package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import java.util.List;

@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping(path = "/users")
public class UserController {
    private final UserService userService;

    @PostMapping
    public UserDto create(@RequestBody UserDto userDto) {
        log.info("Получен Post запрос к эндпоинту: /users");
        return userService.create(userDto);
    }

    @PatchMapping("/{id}")
    public UserDto update(@RequestBody UserDto userDto, @PathVariable("id") Integer id) {
        log.info("Получен Patch запрос к эндпоинту: /users. Обновление пользователя:" + id);
        userDto.setId(id);
        return userService.update(userDto);
    }

    @GetMapping("/{id}")
    public UserDto getUser(@PathVariable(required = true) Integer id) {
        log.info("Получен Get запроск эндпоинту: /users. Запрос элемента с ID = " + id);
        return userService.getUser(id);
    }

    @GetMapping
    public List<User> getAll() {
        log.info("Получен Get запроск эндпоинту: /users");
        return userService.getAll();
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable(required = true) Integer id) {
        log.info("Получен Patch запрос к эндпоинту: /users. Удаление user:" + id);
        userService.delete(id);
    }
}
