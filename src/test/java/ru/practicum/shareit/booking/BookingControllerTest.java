package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.TestHelper;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingControllerTest {
    private final ObjectMapper objectMapper;
    private final MockMvc mockMvc;
    @MockBean
    BookingService service;
    TestHelper test = new TestHelper();

    User user = test.getUser();
    Item item = test.getItem();
    String header = test.getHeader();
    BookingDtoResponse bookingDtoResponse = test.getBookingDtoResponse();
    BookingDto bookingDto = test.getBookingDto();

    @Test
    public void createBookingTest() throws Exception {
        when(service.create(anyLong(), any())).thenReturn(bookingDtoResponse);

        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(header, 1L)
                        .content(objectMapper.writeValueAsBytes(bookingDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.item.id").value(1L))
                .andExpect(jsonPath("$.item.name").value("name"))
                .andExpect(jsonPath("$.booker.name").value("prince"));
    }

    @Test
    public void getByIdTest() throws Exception {
        when(service.getBookingInfo(anyLong(), anyLong())).thenReturn(bookingDtoResponse);

        mockMvc.perform(get("/bookings/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(header, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.item.id").value(1L))
                .andExpect(jsonPath("$.item.name").value("name"))
                .andExpect(jsonPath("$.booker.name").value("prince"));
    }

    @Test
    public void getByBookerTest() throws Exception {
        when(service.getByBooker(anyLong(), anyString(), any()))
                .thenReturn(Collections.singletonList(bookingDtoResponse));

        mockMvc.perform(get("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(header, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].item.id").value(1L))
                .andExpect(jsonPath("$[0].item.name").value("name"))
                .andExpect(jsonPath("$[0].booker.name").value("prince"));
    }

    @Test
    public void getAllByOwnerTest() throws Exception {
        when(service.getByOwner(anyLong(), anyString(), any()))
                .thenReturn(Collections.singletonList(bookingDtoResponse));

        mockMvc.perform(get("/bookings/owner")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(header, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].item.id").value(1L))
                .andExpect(jsonPath("$[0].item.name").value("name"))
                .andExpect(jsonPath("$[0].booker.name").value("prince"));
    }

    @Test
    public void updateTest() throws Exception {
        bookingDtoResponse.setStatus(BookingStatus.APPROVED);
        when(service.changeStatus(anyLong(),anyLong(), anyBoolean())).thenReturn(bookingDtoResponse);

        mockMvc.perform(patch("/bookings/1?approved=true")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(header, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }
}
