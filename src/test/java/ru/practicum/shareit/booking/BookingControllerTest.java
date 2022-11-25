package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
public class BookingControllerTest {

    @Autowired
    private ObjectMapper mapper;
    @MockBean
    private BookingService bookingService;

    @Autowired
    private MockMvc mvc;

    private UserDto  userDto = new UserDto(
            1L,
            "Petr",
            "test@email.com");

    private ItemDto itemDto = new ItemDto(
            1L,
            "Тестовый предмет",
            "Тестовое описание",
            false,
            userDto,
            1L);
    private BookingDto bookingDto = new BookingDto(
            1,
            LocalDateTime.now().plusSeconds(1),
            LocalDateTime.now().plusSeconds(2),
            1L,
            itemDto,
            userDto,
            BookingStatus.APPROVED);

    @Test
    void saveNewBooking() throws Exception {
        when(bookingService.create(any(), anyLong()))
                .thenReturn(bookingDto);

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class));
    }

    @Test
    void updateBooking() throws Exception {
        when(bookingService.create(any(), anyLong()))
                .thenReturn(bookingDto);
        BookingDto bookingDtoUpdate = new BookingDto(
                1,
                LocalDateTime.now().plusSeconds(1),
                LocalDateTime.now().plusSeconds(2),
                1L,
                itemDto,
                userDto,
                BookingStatus.WAITING);

        when(bookingService.update(any(), anyLong()))
                .thenReturn(bookingDtoUpdate);

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus().toString())));

        mvc.perform(patch("/bookings/{id}", 1L)
                        .content(mapper.writeValueAsString(bookingDtoUpdate))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk());

        mvc.perform(patch("/bookings/{id}", 1L)
                        .content(mapper.writeValueAsString(bookingDtoUpdate))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "1")
                        .param("approved","true"))
                .andExpect(status().isOk());
    }

    @Test
    void getBookings() throws Exception {
        when(bookingService.getBooking(anyLong(), anyLong()))
                .thenReturn(bookingDto);

        mvc.perform(get("/bookings/{id}", bookingDto.getId())
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus().toString())));
    }


    @Test
    void deleteBookings() throws Exception {
        mvc.perform(delete("/bookings/{id}", bookingDto.getId())
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk());
    }


    @Test
    void getAllByOwner() throws Exception {
        when(bookingService.getAllByOwner(anyLong(),anyString()))
                .thenReturn(Arrays.asList(bookingDto));

        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].status", is(bookingDto.getStatus().toString())));
    }

    @Test
    void getAllByUser() throws Exception {
        when(bookingService.getAllByUser(anyLong(),anyString()))
                .thenReturn(Arrays.asList(bookingDto));

        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].status", is(bookingDto.getStatus().toString())));
    }

    @Test
    void getAllByOwnerWithState() throws Exception {
        when(bookingService.getAllByOwner(anyLong(),anyString(),any()))
                .thenReturn(Arrays.asList(bookingDto));

        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", "1")
                        .param("state", "All")
                        .param("from", "0")
                        .param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].status", is(bookingDto.getStatus().toString())));
    }

    @Test
    void getAllByUserWithState() throws Exception {
        when(bookingService.getAllByUser(anyLong(),anyString(),any()))
                .thenReturn(Arrays.asList(bookingDto));

        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", "1")
                        .param("state", "All")
                        .param("from", "0")
                        .param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].status", is(bookingDto.getStatus().toString())));

        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", "1")
                        .param("state", "All")
                        .param("from", "-1")
                        .param("size", "-1"))
                .andExpect(status().isBadRequest());
    }

}
