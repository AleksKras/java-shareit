package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.comment.Comment;
import ru.practicum.shareit.comment.CommentMapper;
import ru.practicum.shareit.comment.CommentRepository;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingDTO;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Validated
@Service
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final UserService userService;
    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;
    private final BookingMapper bookingMapper;
    private final UserMapper userMapper;
    private final CommentMapper commentMapper;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    public Item create(ItemDto itemDto, long userId) {
        User user = userService.getUser(userId);
        itemDto.setOwner(userMapper.toDto(user));
        Item item = itemMapper.toItem(itemDto);
        return itemRepository.save(item);
    }

    @Override
    public Comment createComment(CommentDto commentDto, long itemId, long userId) {
        User user = userService.getUser(userId);
        Item item = getItem(itemId);
        Comment comment = commentMapper.toComment(commentDto);
        comment.setItem(item);
        comment.setAuthor(user);
        comment.setCreated(LocalDateTime.now());
        Optional<Booking> booking = bookingRepository.findFirstByItemEqualsAndAndBookerEqualsAndEndBefore(item, user,
                comment.getCreated());
        log.info(booking.toString());
        if (!booking.isPresent()) {
            throw new ValidationException("не найдено бронирование Item");
        }
        return commentRepository.save(comment);
    }

    @Override
    public Item update(ItemDto itemDto, long userId) {
        long itemId = itemDto.getId();
        checkItemOwner(itemId, userId);
        Item item = getItem(itemId);
        itemMapper.updateItemFromDto(itemDto, item);
        return itemRepository.save(item);
    }

    @Override
    public Item getItem(long id) {
        return itemRepository.getReferenceById(id);
    }

    @Override
    public ItemWithBookingDTO getItemWithBooking(long id, long userId) {
        Item item = getItem(id);
        User user = userService.getUser(userId);
        ItemWithBookingDTO itemWithBookingDTO = itemMapper.toDtoWithBooking(item);
        if (item.getOwner().equals(user)) {
            itemWithBookingDTO = addBookingToItem(item);
        }
        List<Comment> commentList = commentRepository.findAllByItemEquals(item);
        itemWithBookingDTO.setComments(listCommentToDto(commentList));
        return itemWithBookingDTO;
    }

    @Override
    public List<ItemWithBookingDTO> getAll(long userid) {
        User user = userService.getUser(userid);
        List<ItemWithBookingDTO> listItemWithBookingDTO = new ArrayList<>();
        for (Item item : itemRepository.findByOwner(user)) {
            ItemWithBookingDTO itemWithBookingDTO = addBookingToItem(item);
            List<Comment> commentList = commentRepository.findAllByItemEquals(item);
            itemWithBookingDTO.setComments(listCommentToDto(commentList));
            listItemWithBookingDTO.add(itemWithBookingDTO);
        }
        return listItemWithBookingDTO;
    }

    @Override
    public List<ItemDto> search(String query, long userId) {
        User user = userService.getUser(userId);
        List<Item> listItem = new ArrayList<>();
        if (StringUtils.isNotBlank(query)) {
            listItem = itemRepository.search(query);
        }
        return listItemToDto(listItem);
    }

    @Override
    public void delete(long itemId) {
        Item item = getItem(itemId);
        itemRepository.delete(item);
    }

    @Override
    public void checkItemOwner(long itemId, long userId) {
        User user = userService.getUser(userId);
        Item item = getItem(itemId);
        if (!user.equals(item.getOwner())) {
            throw new NotFoundException("Пользователь с ID=" + userId + " не является владельцем");
        }
    }

    private List<ItemDto> listItemToDto(List<Item> listItem) {
        List<ItemDto> listItemDto = new ArrayList<>();
        for (Item item : listItem) {
            listItemDto.add(itemMapper.toDto(item));
        }
        return listItemDto;
    }

    private List<CommentDto> listCommentToDto(List<Comment> listComment) {
        List<CommentDto> listCommentDto = new ArrayList<>();
        for (Comment comment : listComment) {
            listCommentDto.add(commentMapper.toDto(comment));
        }
        return listCommentDto;
    }

    private ItemWithBookingDTO addBookingToItem(Item item) {
        Booking lastBooking = bookingRepository.findFirstByItemEqualsAndEndBeforeOrderByEndDesc
                (item, LocalDateTime.now());
        Booking nextBooking = bookingRepository.findFirstByItemEqualsAndStartAfterOrderByStartAsc
                (item, LocalDateTime.now());
        ItemWithBookingDTO itemWithBookingDTO = itemMapper.toDtoWithBooking(item);
        itemWithBookingDTO.setLastBooking(bookingMapper.toShortDto(lastBooking));
        itemWithBookingDTO.setNextBooking(bookingMapper.toShortDto(nextBooking));
        return itemWithBookingDTO;
    }

}
