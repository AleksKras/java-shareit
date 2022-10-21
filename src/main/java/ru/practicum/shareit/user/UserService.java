package ru.practicum.shareit.user;

import ru.practicum.shareit.exception.*;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@Data
public class UserService {
    private Map<Long,User> userMap = new HashMap<>();
    private long id;

    public User create(User user){
        id++;
        user.setId(id);
        userMap.put(id,user);
        return user;
    }

    public User update(User user){
        userMap.put(id,user);
        return user;
    }

    public User getUser(long id) {
        if (userMap.containsKey(id)) {
            return userMap.get(id);
        } else {
            log.info("Пользователь с идентификатором {} не найден.", id);
            throw new NotFoundException("Пользователь с ID=" + id + "не найден");
        }
    }

    public List<User> getAll(){
        return new ArrayList<User>(userMap.values());
    }

    public void delete(long id) {
        User user = getUser(id);
        userMap.remove(id);
    }

}
