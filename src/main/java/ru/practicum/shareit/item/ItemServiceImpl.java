package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@Validated
@Service
public class ItemServiceImpl implements ItemService {
    private UserService userService;
    private ItemDao itemDao;
    private ItemMapper mapper;


    @Autowired
    public ItemServiceImpl(UserService userService, ItemDao itemDao) {
        this.userService = userService;
        this.itemDao = itemDao;
        this.mapper = new ItemMapperImpl();
    }

    public Item create(ItemDto itemDto, long userId) {
        User user = userService.getUser(userId);
        itemDto.setOwner(user);
        Item item = mapper.toItem(itemDto);
        return itemDao.create(item);
    }

    public Item update(ItemDto itemDto, long userId) {
        long itemId = itemDto.getId();
        checkItemOwner(itemId, userId);
        Item item = getItem(itemId);
        mapper.updateItemFromDto(itemDto, item);
        return itemDao.update(item);
    }

    public Item getItem(long id) {
        return itemDao.get(id);
    }

    public List<Item> getAll(long userid) {
        return itemDao.getAll(userid);
    }

    public List<Item> search(String query, long userId) {
        User user = userService.getUser(userId);
        return itemDao.search(query);
    }

    public void delete(long itemId) {
        itemDao.delete(itemId);
    }

    public void checkItemOwner(long itemId, long userId) {
        User user = userService.getUser(userId);
        Item item = getItem(itemId);
        if (!user.equals(item.getOwner())) {
            throw new NotFoundException("Пользователь с ID=" + userId + " не является владельцем");
        }

    }

}
