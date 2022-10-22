package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.*;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

@Slf4j
@Service
@Data
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    @Autowired
    private UserDao userDao;
    @Autowired
    private UserMapper mapper;

    public User create(User user) {
        checkUserEmail(user);
        return userDao.create(user);
    }

    public User update(UserDto userDto) {
        User user = getUser(userDto.getId());
        checkUserEmail(mapper.toUser(userDto));
        mapper.updateUserFromDto(userDto, user);
        return userDao.update(user);
    }

    public User getUser(long id) {
        return userDao.getUser(id);
    }

    public List<User> getAll() {
        return userDao.getAll();
    }

    public void delete(long userId) {
        User user = getUser(userId);
        userDao.delete(userId);
    }

    public void checkUserEmail(User user) {
        String email = user.getEmail();
        long userId = user.getId();
        for (User mapUser : getAll()) {
            if (mapUser.getEmail().equals(email) & mapUser.getId() != userId) {
                log.info("Пользователь с email {} уже существует", email);
                throw new EmailValidationException("Пользователь с email " + email + " уже существует\"");
            }
        }
    }
}
