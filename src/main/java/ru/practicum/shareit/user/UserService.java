package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    User create(User user);

    User update(UserDto userDto);

    User getUser(long id);

    List<User> getAll();

    void delete(long userId);

    void checkUserEmail(User user);
}
