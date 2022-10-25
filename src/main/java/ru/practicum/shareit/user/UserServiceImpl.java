package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.*;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserDao userDao;
    private final UserMapper mapper;

    @Override
    public User create(User user) {
        checkUserEmail(user);
        return userDao.create(user);
    }

    @Override
    public User update(UserDto userDto) {
        User user = getUser(userDto.getId());
        checkUserEmail(mapper.toUser(userDto));
        mapper.updateUserFromDto(userDto, user);
        return userDao.update(user);
    }

    @Override
    public User getUser(long id) {
        return userDao.getUser(id);
    }

    @Override
    public List<User> getAll() {
        return userDao.getAll();
    }

    @Override
    public void delete(long userId) {
        User user = getUser(userId);
        userDao.delete(userId);
    }

    private void checkUserEmail(User user) {
        String email = user.getEmail();
        long userId = user.getId();
        for (User mapUser : getAll()) {
            if (StringUtils.equalsIgnoreCase(mapUser.getEmail(), email) && mapUser.getId() != userId) {
                log.info("Пользователь с email {} уже существует", email);
                throw new EmailValidationException("Пользователь с email " + email + " уже существует\"");
            }
        }
    }
}
