package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.comment.Comment;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import javax.persistence.*;
import javax.transaction.Transactional;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

import org.exparity.hamcrest.date.LocalDateTimeMatchers;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Slf4j
@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemServiceImplTest {

    private final EntityManager em;
    private final ItemService service;

    private final UserService userService;

    private final BookingService bookingService;
    private final ItemMapper mapper;

    private UserDto userDto;

    @Test
    void saveItem() {
        userDto = userService.create(makeUserDto("some@email.com", "Пётр"));
        ItemDto itemDto = makeItemDto("Название", "описание", true, userDto, null);
        ItemDto itemDtoCreated = service.create(itemDto, userDto.getId());
        // then
        TypedQuery<Item> query = em.createQuery("Select i from Item i where i.id = :id", Item.class);
        Item item = query.setParameter("id", itemDtoCreated.getId())
                .getSingleResult();

        assertThat(item.getId(), notNullValue());
        assertThat(item.getName(), equalTo(itemDto.getName()));
        assertThat(item.getDescription(), equalTo(itemDto.getDescription()));
        assertThat(item.getAvailable(), equalTo(itemDto.getAvailable()));
        assertThat(item.getOwner().getId(), equalTo(itemDto.getOwner().getId()));
        assertThat(item.getRequestId(), equalTo(itemDto.getRequestId()));
    }

    @Test
    void saveCommentToItem() throws InterruptedException {
        userDto = userService.create(makeUserDto("some@email.com", "Пётр"));
        UserDto userDtoBooking = userService.create(makeUserDto("someOne@email.com", "Пётр1"));
        ItemDto itemDto = makeItemDto("Название", "описание", true, userDto, null);
        ItemDto itemDtoCreated = service.create(itemDto, userDto.getId());
        Long itemId = itemDtoCreated.getId();

        BookingDto bookingDto = new BookingDto(
                1,
                LocalDateTime.now().plusSeconds(1),
                LocalDateTime.now().plusSeconds(2),
                itemId,
                itemDtoCreated,
                userDtoBooking,
                BookingStatus.APPROVED);
        bookingService.create(bookingDto, userDtoBooking.getId());

        TimeUnit.SECONDS.sleep(3);

        CommentDto commentDto = service.createComment(makeCommentDto(
                1L,
                "Текст комментария",
                "Petr",
                LocalDateTime.now()), itemId, userDtoBooking.getId());

        // then
        TypedQuery<Comment> query = em.createQuery("Select c from Comment c where c.id = :id", Comment.class);
        Comment comment = query.setParameter("id", commentDto.getId())
                .getSingleResult();

        assertThat(comment.getId(), notNullValue());
        assertThat(comment.getText(), equalTo(commentDto.getText()));
        assertThat(comment.getAuthor().getName(), equalTo(commentDto.getAuthorName()));
        assertThat(comment.getCreated(), LocalDateTimeMatchers.before(LocalDateTime.now()));

        Exception exception = new Exception();
        try {

            commentDto = service.createComment(makeCommentDto(
                    2L,
                    "Текст комментария",
                    "Petr",
                    LocalDateTime.now()), itemId, userDto.getId());

        } catch (ValidationException e) {
            exception = e;
        }
        assertThat(ValidationException.class, equalTo(exception.getClass()));
    }

    @Test
    void updateItem() {
        // given
        userDto = userService.create(makeUserDto("some@email.com", "Пётр"));
        ItemDto itemDto = makeItemDto("Название", "описание", true, userDto, null);

        // when
        ItemDto createdItem = service.create(itemDto, userDto.getId());

        TypedQuery<Item> query = em.createQuery("Select i from Item i where i.id = :id", Item.class);
        Item item = query.setParameter("id", createdItem.getId())
                .getSingleResult();


        createdItem.setName("Новое название");

        service.update(createdItem, userDto.getId());

        // then
        query = em.createQuery("Select i from Item i where i.id = :id", Item.class);
        Item itemAfterUpdate = query.setParameter("id", createdItem.getId())
                .getSingleResult();

        assertThat(itemAfterUpdate.getId(), notNullValue());
        assertThat(itemAfterUpdate.getId(), equalTo(createdItem.getId()));
        assertThat(itemAfterUpdate.getName(), equalTo(createdItem.getName()));
        assertThat(itemAfterUpdate.getDescription(), equalTo(createdItem.getDescription()));
    }

    @Test
    void getItem() {
        // given
        userDto = userService.create(makeUserDto("some@email.com", "Пётр"));
        ItemDto itemDto = makeItemDto("Название", "описание", true, userDto, null);

        // when
        ItemDto createdItem = service.create(itemDto, userDto.getId());

        // then

        Item item = mapper.toItem(service.getItem(createdItem.getId()));


        Exception exception = new Exception();
        try {
            Item notFoundItem = mapper.toItem(service.getItem(createdItem.getId() + 1));
        } catch (EntityNotFoundException e) {
            exception = e;
        }

        assertThat(item.getId(), notNullValue());
        assertThat(item.getName(), equalTo(itemDto.getName()));
        assertThat(item.getDescription(), equalTo(itemDto.getDescription()));
        assertThat(EntityNotFoundException.class, equalTo(exception.getClass()));
    }

    @Test
    void deleteItem() {
        // given
        userDto = userService.create(makeUserDto("some@email.com", "Пётр"));
        ItemDto itemDto = makeItemDto("Название", "описание", true, userDto, null);

        // when
        ItemDto createdItem = service.create(itemDto, userDto.getId());

        // then
        TypedQuery<Item> query = em.createQuery("Select u from Item u where u.id = :id", Item.class);
        Item item = query.setParameter("id", createdItem.getId())
                .getSingleResult();

        assertThat(item.getId(), notNullValue());
        assertThat(item.getName(), equalTo(itemDto.getName()));


        service.delete(createdItem.getId());

        query = em.createQuery("Select u from Item u where u.id = :id", Item.class);
        String exceptionMessage = "";
        try {
            Item deletedItem = query.setParameter("id", createdItem.getId())
                    .getSingleResult();
        } catch (NoResultException e) {
            exceptionMessage = "Данные не найдены";
        }

        assertThat(exceptionMessage, equalTo("Данные не найдены"));
    }

    /*
        @Test
        void getAllItems() {
            // given
            List<ItemDto> sourceItems = List.of(
                    makeItemDto("ivan@email", "Ivan"),
                    makeItemDto("petr@email", "Petr"),
                    makeItemDto("vasilii@email", "Vasilii"));

            for (ItemDto item : sourceItems) {
                service.create(item);
            }

            // when
            List<Item> targetItems = service.getAll();

            // then
            assertThat(targetItems, hasSize(sourceItems.size()));
            for (ItemDto sourceItem : sourceItems) {
                assertThat(targetItems, hasItem(allOf(
                        hasProperty("id", notNullValue()),
                        hasProperty("name", equalTo(sourceItem.getName())),
                        hasProperty("email", equalTo(sourceItem.getEmail()))
                )));
            }
        }



    */
    private ItemDto makeItemDto(String name, String description, Boolean available, UserDto owner, Long requestId) {
        ItemDto dto = new ItemDto();
        dto.setName(name);
        dto.setDescription(description);
        dto.setAvailable(available);
        dto.setOwner(owner);
        dto.setRequestId(requestId);
        return dto;
    }

    private UserDto makeUserDto(String email, String name) {
        UserDto dto = new UserDto();
        dto.setEmail(email);
        dto.setName(name);
        return dto;
    }

    private CommentDto makeCommentDto(Long id, String text, String authorName, LocalDateTime created) {
        CommentDto commentDto = new CommentDto();
        commentDto.setId(id);
        commentDto.setText(text);
        commentDto.setAuthorName(authorName);
        commentDto.setCreated(created);
        return commentDto;
    }

}