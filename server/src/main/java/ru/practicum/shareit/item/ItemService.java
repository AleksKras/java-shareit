package ru.practicum.shareit.item;

import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingDTO;

import java.util.List;

public interface ItemService {
    ItemDto create(ItemDto itemDto, long userId);

    CommentDto createComment(CommentDto commentDto, long itemId, long userId);

    ItemDto update(ItemDto itemDto, long userId);

    ItemDto getItem(long id);

    ItemWithBookingDTO getItemWithBooking(long id, long userId);

    List<ItemWithBookingDTO> getAll(long userid);

    List<ItemDto> search(String query, long userId);

    void delete(long itemId);

    void checkItemOwner(long itemId, long userId);

}
