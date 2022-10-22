package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    Item create(ItemDto itemDto, long userId);

    Item update(ItemDto itemDto, long userId);

    Item getItem(long id);

    List<Item> getAll(long userid);

    List<Item> search(String query, long userId);

    void delete(long itemId);

    void checkItemOwner(long itemId, long userId);

}
