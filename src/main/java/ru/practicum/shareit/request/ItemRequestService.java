package ru.practicum.shareit.request;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.comment.Comment;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingDTO;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto create(ItemRequestDto itemRequestDto, long userId);

    ItemRequestDto getRequest(long id, long userId);

    List<ItemRequestDto> getAllByUser(long userid);

    List<ItemRequestDto> getAll(long userid, Pageable pageable);

    void delete(long itemRequestId, long userid);

}
