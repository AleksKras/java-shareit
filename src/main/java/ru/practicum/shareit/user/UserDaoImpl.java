package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.exception.NotFoundException;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@Validated
public class UserDaoImpl implements UserDao {
    private Map<Long, User> userMap = new HashMap<>();

    private long id;

    @Override
    public User create(@Valid User user) {
        id++;
        user.setId(id);
        userMap.put(id, user);
        return user;
    }

    @Override
    public User update(@Valid User user) {
        userMap.put(user.getId(), user);
        return user;
    }

    @Override
    public User getUser(long id) {
        if (userMap.containsKey(id)) {
            return userMap.get(id);
        } else {
            log.info("Пользователь с идентификатором {} не найден.", id);
            throw new NotFoundException("Пользователь с ID=" + id + " не найден");
        }
    }

    @Override
    public List<User> getAll() {
        return new ArrayList<User>(userMap.values());
    }

    @Override
    public void delete(long userId) {
        userMap.remove(userId);
    }
}
