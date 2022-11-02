package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.util.List;

@Validated
@Service
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final UserService userService;
    private final ItemDao itemDao;
    private final ItemMapper mapper;

    @Override
    public Item create(ItemDto itemDto, long userId) {
        User user = userService.getUser(userId);
        itemDto.setOwner(user);
        Item item = mapper.toItem(itemDto);
        return itemDao.create(item);
    }

    @Override
    public Item update(ItemDto itemDto, long userId) {
        long itemId = itemDto.getId();
        checkItemOwner(itemId, userId);
        Item item = getItem(itemId);
        mapper.updateItemFromDto(itemDto, item);
        return itemDao.update(item);
    }

    @Override
    public Item getItem(long id) {
        return itemDao.get(id);
    }

    @Override
    public List<Item> getAll(long userid) {
        return itemDao.getAll(userid);
    }

    @Override
    public List<Item> search(String query, long userId) {
        User user = userService.getUser(userId);
        return itemDao.search(query);
    }

    @Override
    public void delete(long itemId) {
        itemDao.delete(itemId);
    }

    @Override
    public void checkItemOwner(long itemId, long userId) {
        User user = userService.getUser(userId);
        Item item = getItem(itemId);
        if (!user.equals(item.getOwner())) {
            throw new NotFoundException("Пользователь с ID=" + userId + " не является владельцем");
        }
    }

}
