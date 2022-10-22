package ru.practicum.shareit.user;

import javax.validation.Valid;
import java.util.List;

public interface UserDao {
    User create(@Valid User user);

    User update(@Valid User user);

    User getUser(long id);

    List<User> getAll();

    void delete(long userId);
}
