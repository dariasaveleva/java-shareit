package ru.practicum.shareit.requests;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.TestHelper;
import ru.practicum.shareit.request.ItemRequestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.service.RequestService;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class RequestControllerTest {
    private final ObjectMapper objectMapper;
    private final MockMvc mockMvc;
    @MockBean
    RequestService requestService;
    TestHelper test = new TestHelper();
    ItemRequestDto itemRequestDto = test.getItemRequestDto();
    ItemRequestResponseDto itemRequestResponseDto = test.getItemRequestResponseDto();

    @Test
    void createRequestTest() throws Exception {
        when(requestService.createRequest(anyLong(), any())).thenReturn(itemRequestDto);

        mockMvc.perform(post("/requests")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id", 1L)
                .content(objectMapper.writeValueAsBytes(itemRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.requester_id").value(1L))
                .andExpect(jsonPath("$.description").value("нужна щетка для обуви"));
    }

    @Test
    void getAllRequestsInfoTest() throws Exception {
        when(requestService.getAllRequestsInfo(anyLong()))
                .thenReturn(Collections.singletonList(itemRequestResponseDto));

        mockMvc.perform(get("/requests")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].requester_id").value(1L))
                .andExpect(jsonPath("$[0].description").value("нужна щетка для обуви"));
    }

    @Test
    void getRequestsListTest() throws Exception {
        when(requestService.getRequestsList(anyLong(), anyInt(), anyInt()))
                .thenReturn(Collections.singletonList(itemRequestResponseDto));

        mockMvc.perform(get("/requests/all")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].requester_id").value(1L))
                .andExpect(jsonPath("$[0].description").value("нужна щетка для обуви"));
    }

    @Test
    void getOneRequestInfoTest() throws Exception {
        when(requestService.getOneRequestInfo(anyLong(), anyLong())).thenReturn(itemRequestResponseDto);

        mockMvc.perform(get("/requests/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.requester_id").value(1L))
                .andExpect(jsonPath("$.description").value("нужна щетка для обуви"));
    }
}
