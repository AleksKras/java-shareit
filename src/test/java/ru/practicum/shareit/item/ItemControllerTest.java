package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.comment.Comment;
import ru.practicum.shareit.comment.CommentMapper;
import ru.practicum.shareit.comment.CommentMapperImpl;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingDTO;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
public class ItemControllerTest {

    @Autowired
    private ObjectMapper mapper;
    @MockBean
    private ItemService itemService;

    @Autowired
    private MockMvc mvc;

    private ItemMapper itemMapper = new ItemMapperImpl();
    private CommentMapper commentMapper = new CommentMapperImpl();

    private ItemDto itemDto = new ItemDto(
            1L,
            "Тестовый предмет",
            "Тестовое описание",
            false,
            new UserDto(1L,
                    "Petr",
                    "test@email.com"),
            1L);

    private ItemWithBookingDTO itemWithBookingDTO = new ItemWithBookingDTO(
            1L,
            "Тестовый предмет",
            "Тестовое описание",
            false,
            new UserDto(1L,
                    "Petr",
                    "test@email.com"),
            new BookingShortDto(1L, 1L),
            new BookingShortDto(1L, 1L),
            null,
            null);

    private CommentDto commentDto = new CommentDto(
            1L,
            "Текст комментария",
            "Petr",
            LocalDateTime.now());

    @Test
    void saveNewItem() throws Exception {
        when(itemService.create(any(), anyLong()))
                .thenReturn(itemDto);

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())));
    }

    @Test
    void updateItem() throws Exception {
        when(itemService.create(any(), anyLong()))
                .thenReturn(itemDto);
        ItemDto itemDtoUpdate = new ItemDto(1L,
                "Тестовый обновленный предмет",
                "Тестовое описание",
                false,
                new UserDto(1L,
                        "Petr",
                        "test@email.com"),
                1L);

        when(itemService.update(any(), anyLong()))
                .thenReturn(itemDtoUpdate);

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())));

        mvc.perform(patch("/items/{id}", 1L)
                        .content(mapper.writeValueAsString(itemDtoUpdate))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk());
    }

    @Test
    void getItems() throws Exception {
        when(itemService.getItemWithBooking(anyLong(), anyLong()))
                .thenReturn(itemWithBookingDTO);

        mvc.perform(get("/items/{id}", itemDto.getId())
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())));
    }

    @Test
    void saveComment() throws Exception {
        when(itemService.createComment(any(), anyLong(), anyLong()))
                .thenReturn(commentDto);

        mvc.perform(post("/items/{id}/comment", itemDto.getId())
                        .content(mapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentDto.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(commentDto.getText())))
                .andExpect(jsonPath("$.authorName", is(commentDto.getAuthorName())));
    }

    @Test
    void searchItemWithoutParams() throws Exception {
        when(itemService.search(anyString(), anyLong()))
                .thenReturn(Arrays.asList(itemDto));

        mvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void searchItem() throws Exception {
        when(itemService.search(anyString(), anyLong()))
                .thenReturn(Arrays.asList(itemDto));

        mvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", "1")
                        .param("text", "тест"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(itemWithBookingDTO.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemWithBookingDTO.getName())))
                .andExpect(jsonPath("$[0].description", is(itemWithBookingDTO.getDescription())));
    }

    @Test
    void deleteItems() throws Exception {
        mvc.perform(delete("/items/{id}", itemDto.getId())
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk());
    }

    @Test
    void getAllItems() throws Exception {
        when(itemService.getAll(anyLong()))
                .thenReturn(Arrays.asList(itemWithBookingDTO));

        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(itemWithBookingDTO.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemWithBookingDTO.getName())))
                .andExpect(jsonPath("$[0].description", is(itemWithBookingDTO.getDescription())));
    }

    @Test
    void testDtoMapper() throws Exception {
        Item item = itemMapper.toItem(null);
        assertThat(item, is(nullValue()));
        ItemDto itemDtoNull = itemMapper.toDto(null);
        assertThat(itemDtoNull, is(nullValue()));
        ItemWithBookingDTO itemWithBookingDTO = itemMapper.toDtoWithBooking(null);
        assertThat(itemWithBookingDTO, is(nullValue()));
        Comment comment = commentMapper.toComment(null);
        assertThat(comment, is(nullValue()));
        CommentDto commentDtoNull = commentMapper.toDto(null);
        assertThat(commentDtoNull, is(nullValue()));
    }

}
