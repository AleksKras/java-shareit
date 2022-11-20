package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Validated
@Service
@AllArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final UserService userService;
    private final ItemRequestRepository itemRequestRepository;
    private final ItemRequestMapper itemRequestMapper;
    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;
    private final UserMapper userMapper;


    @Override
    public ItemRequestDto create(ItemRequestDto itemRequestDto, long userId) {
        User user = userMapper.toUser(userService.getUser(userId));
        log.info("Создание запроса от пользователя: " + user.toString());
        ItemRequest itemRequest = itemRequestMapper.toItemRequest(itemRequestDto);
        itemRequest.setRequestor(user);
        itemRequest.setCreated(LocalDateTime.now());
        return itemRequestMapper.toDto(itemRequestRepository.save(itemRequest));
    }

    @Override
    public ItemRequestDto getRequest(long id, long userId) {
        User user = userMapper.toUser(userService.getUser(userId));
        log.info("Запрос от пользователя: " + user.toString());
        ItemRequest itemRequest = itemRequestRepository.getReferenceById(id);
        return addItemDtoList(itemRequest);
    }

    @Override
    public List<ItemRequestDto> getAllByUser(long userid) {
        User user = userMapper.toUser(userService.getUser(userid));
        log.info("Запрос от пользователя: " + user.toString());
        List<ItemRequest> itemRequestList = itemRequestRepository.findAllByRequestorEqualsOrderByCreated(user);
        return getItemRequestDtoList(itemRequestList);
    }

    @Override
    public List<ItemRequestDto> getAll(long userid, Pageable pageable) {
        User user = userMapper.toUser((userService.getUser(userid)));
        Page<ItemRequest> pageItemRequest = itemRequestRepository.findAllByRequestorIsNot(user, pageable);
        List<ItemRequest> itemRequestList = new ArrayList<>();
        if(pageItemRequest != null && pageItemRequest.hasContent()) {
            itemRequestList = pageItemRequest.getContent();
        }
        return getItemRequestDtoList(itemRequestList);
    }

    @Override
    public void delete(long itemRequestId, long userid) {
        ItemRequest itemRequest = itemRequestRepository.getReferenceById(itemRequestId);
        itemRequestRepository.delete(itemRequest);
    }

    private List<ItemRequestDto> getItemRequestDtoList(List<ItemRequest> listItemRequest) {
        List<ItemRequestDto> itemRequestDtoList = new ArrayList<>();
        for (ItemRequest itemRequest : listItemRequest) {
            itemRequestDtoList.add(addItemDtoList(itemRequest));
        }
        return itemRequestDtoList;
    }

    private ItemRequestDto addItemDtoList(ItemRequest itemRequest) {
        ItemRequestDto itemRequestDto = itemRequestMapper.toDto(itemRequest);
        List<Item> itemList = itemRepository.findAllByRequestId(itemRequest.getId());
        itemRequestDto.setItems(listItemToDto(itemList));
        return itemRequestDto;
    }

    private List<ItemDto> listItemToDto(List<Item> listItem) {
        List<ItemDto> listItemDto = new ArrayList<>();
        for (Item item : listItem) {
            listItemDto.add(itemMapper.toDto(item));
        }
        return listItemDto;
    }

}


