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
    public UserDto create(UserDto userDto) {
        return mapper.toDto(userRepository.save(mapper.toUser(userDto)));
    }

    @Override
    public UserDto update(UserDto userDto) {
        User user = userRepository.getReferenceById(userDto.getId());
        mapper.updateUserFromDto(userDto, user);
        return mapper.toDto(userRepository.save(user));
    }

    @Override
    public UserDto getUser(long id) {
        return mapper.toDto(userRepository.getReferenceById(id));
    }

    @Override
    public List<User> getAll() {
        return userRepository.findAll();
    }

    @Override
    public void delete(long userId) {
        User user = userRepository.getReferenceById(userId);
        userRepository.delete(user);
    }

}
