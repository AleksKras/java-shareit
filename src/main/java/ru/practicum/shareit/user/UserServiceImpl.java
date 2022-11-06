package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper mapper;

    @Override
    public User create(User user) {
        return userRepository.save(user);
    }

    @Override
    public User update(UserDto userDto) {
        User user = getUser(userDto.getId());
        mapper.updateUserFromDto(userDto, user);
        return userRepository.save(user);
    }

    @Override
    public User getUser(long id) {
        return userRepository.getReferenceById(id);
    }

    @Override
    public List<User> getAll() {
        return userRepository.findAll();
    }

    @Override
    public void delete(long userId) {
        User user = getUser(userId);
        userRepository.delete(user);
    }

}
