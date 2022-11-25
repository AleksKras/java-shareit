package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRequestServiceImplTest {

    private final EntityManager em;
    private final ItemRequestService service;
    private final UserService userService;
    private final ItemService itemService;
    private final ItemRequestMapper mapper;

    @Test
    void saveItemRequest() {
        // given
        ItemRequestDto itemRequestDto = makeItemRequestDto("тестовый запрос",
                LocalDateTime.now(),
                "test1@yandex.ru");
        UserDto userDto = userService.create(new UserDto(
                0L,
                "Petr",
                "test1@email.com"));

        // when
        ItemRequestDto createdItemRequestDto = service.create(itemRequestDto, userDto.getId());

        // then
        TypedQuery<ItemRequest> query = em.createQuery("Select u from ItemRequest u where u.id = :id", ItemRequest.class);
        ItemRequest itemRequest = query.setParameter("id", createdItemRequestDto.getId())
                .getSingleResult();

        assertThat(itemRequest.getId(), notNullValue());
        assertThat(itemRequest.getDescription(), equalTo(itemRequestDto.getDescription()));
    }

    @Test
    void getItemRequest() {
        // given
        ItemRequestDto itemRequestDto = makeItemRequestDto(
                "тестовый запрос",
                LocalDateTime.now(),
                "test2@yandex.ru");
        UserDto userDto = userService.create(new UserDto(
                0L,
                "Petr",
                "test1@email.com"));

        // when
        ItemRequestDto createdItemRequest = service.create(itemRequestDto, userDto.getId());

        // then

        ItemDto itemDto = itemService.create(new ItemDto(
                1L,
                "Тестовый предмет",
                "Тестовое описание",
                true,
                userDto,
                createdItemRequest.getId()), userDto.getId());

        ItemRequest itemRequest = mapper.toItemRequest(service.getRequest(createdItemRequest.getId(), userDto.getId()));

        Assertions.assertThrows(EntityNotFoundException.class, () -> {
            ItemRequest notFoundItemRequest = mapper.toItemRequest(service.getRequest(
                    createdItemRequest.getId() + 1,
                    userDto.getId()));
        });

        assertThat(itemRequest.getId(), notNullValue());
        assertThat(itemRequest.getDescription(), equalTo(itemRequestDto.getDescription()));
    }


    @Test
    void getAllByUserItemRequests() {
        // given
        List<ItemRequestDto> sourceItemRequests = new ArrayList<>();
        sourceItemRequests.add(makeItemRequestDto("тестовый запрос 1", LocalDateTime.now(), "test1@yandex.ru"));
        sourceItemRequests.add(makeItemRequestDto("тестовый запрос 2", LocalDateTime.now(), "test2@yandex.ru"));
        sourceItemRequests.add(makeItemRequestDto("тестовый запрос 3", LocalDateTime.now(), "test3@yandex.ru"));

        UserDto userDto = userService.create(new UserDto(
                0L,
                "Petr",
                "test2@email.com"));

        for (ItemRequestDto itemRequest : sourceItemRequests) {
            service.create(itemRequest, userDto.getId());
        }

        // when
        List<ItemRequestDto> targetItemRequests = service.getAllByUser(userDto.getId());

        // then
        assertThat(targetItemRequests, hasSize(sourceItemRequests.size()));
        for (ItemRequestDto sourceItemRequest : sourceItemRequests) {
            assertThat(targetItemRequests, hasItem(allOf(
                    hasProperty("id", notNullValue()),
                    hasProperty("description", equalTo(sourceItemRequest.getDescription()))
            )));
        }
    }

    @Test
    void getAllItemRequests() {
        // given
        List<ItemRequestDto> sourceItemRequests = new ArrayList<>();
        sourceItemRequests.add(makeItemRequestDto("тестовый запрос 1", LocalDateTime.now(), "test1@yandex.ru"));
        sourceItemRequests.add(makeItemRequestDto("тестовый запрос 2", LocalDateTime.now(), "test2@yandex.ru"));
        sourceItemRequests.add(makeItemRequestDto("тестовый запрос 3", LocalDateTime.now(), "test3@yandex.ru"));

        UserDto userDto = userService.create(new UserDto(
                0L,
                "Petr",
                "test3@email.com"));

        for (ItemRequestDto itemRequest : sourceItemRequests) {
            service.create(itemRequest, userDto.getId());
        }

        userDto = userService.create(new UserDto(
                0L,
                "Petr",
                "test4@email.com"));
        // when
        List<ItemRequestDto> targetItemRequests = service.getAll(userDto.getId(),
                PageRequest.of(0, 3, Sort.by("created").descending()));

        // then
        assertThat(targetItemRequests, hasSize(sourceItemRequests.size()));
        for (ItemRequestDto sourceItemRequest : sourceItemRequests) {
            assertThat(targetItemRequests, hasItem(allOf(
                    hasProperty("id", notNullValue()),
                    hasProperty("description", equalTo(sourceItemRequest.getDescription()))
            )));
        }
    }

    @Test
    void deleteItemRequest() {
        // given
        ItemRequestDto itemRequestDto = makeItemRequestDto(
                "тестовый запрос",
                LocalDateTime.now(),
                "test1@yandex.ru");
        UserDto userDto = userService.create(new UserDto(
                0L,
                "Petr",
                "test1@email.com"));

        // when
        ItemRequestDto createdItemRequestDto = service.create(itemRequestDto, userDto.getId());

        // then
        TypedQuery<ItemRequest> query = em.createQuery("Select u from ItemRequest u where u.id = :id", ItemRequest.class);
        ItemRequest itemRequest = query.setParameter("id", createdItemRequestDto.getId())
                .getSingleResult();

        assertThat(itemRequest.getId(), notNullValue());
        assertThat(itemRequest.getId(), notNullValue());
        assertThat(itemRequest.getDescription(), equalTo(itemRequestDto.getDescription()));

        service.delete(createdItemRequestDto.getId(), userDto.getId());

        Assertions.assertThrows(NoResultException.class, () -> {
            TypedQuery<ItemRequest> typedQuery = em.createQuery("Select u from ItemRequest u where u.id = :id", ItemRequest.class);
            ItemRequest deletedItemRequest = typedQuery.setParameter("id", createdItemRequestDto.getId())
                    .getSingleResult();
        });
    }

    private ItemRequestDto makeItemRequestDto(String description, LocalDateTime created, String email) {
        UserDto userDto = new UserDto();
        userDto.setName("Petr");
        userDto.setEmail(email);
        userDto = userService.create(userDto);


        ItemDto itemDto = itemService.create(new ItemDto(
                1L,
                "Тестовый предмет",
                "Тестовое описание",
                false,
                userDto,
                null), userDto.getId());

        ItemRequestDto itemRequestDto = new ItemRequestDto(
                0,
                description,
                created,
                Arrays.asList(itemDto));

        return itemRequestDto;
    }

}