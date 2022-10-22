package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

@RestController
@Slf4j
public class UserController {
    private final UserService userService;
    private UserMapper mapper;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
        this.mapper = new UserMapperImpl();
    }

    @PostMapping("/users")
    public UserDto create(@RequestBody UserDto userDto) {
        User user = mapper.toUser(userDto);
        log.info("Получен Post запрос к эндпоинту: /users");
        return mapper.toDto(userService.create(user));
    }

    @PatchMapping("/users/{id}")
    public UserDto update(@RequestBody UserDto userDto, @PathVariable("id") Integer id) {
        log.info("Получен Patch запрос к эндпоинту: /users. Обновление пользователя:" + id);
        userDto.setId(id);
        return mapper.toDto(userService.update(userDto));
    }

    @GetMapping("/users/{id}")
    public UserDto getUser(@PathVariable(required = true) Integer id) {
        return mapper.toDto(userService.getUser(id));
    }

    @GetMapping("/users")
    public List<User> getAll() {
        log.info("Получен Get запроск эндпоинту: /users");
        return userService.getAll();
    }

    @DeleteMapping("/users/{id}")
    public void delete(@PathVariable(required = true) Integer id) {
        userService.delete(id);
    }
}
