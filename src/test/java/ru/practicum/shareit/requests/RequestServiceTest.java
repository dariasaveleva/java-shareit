package ru.practicum.shareit.requests;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.TestHelper;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.request.service.RequestServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RequestServiceTest {
    @Mock
    ItemRepository itemRepository;
    @Mock
    RequestRepository requestRepository;
    @Mock
    UserRepository userRepository;
    @InjectMocks
    RequestServiceImpl service;
    TestHelper test = new TestHelper();

    User user = test.getUser();
    Item item = test.getItem();
    ItemRequestDto itemRequestDto = test.getItemRequestDto();
    Request request = test.getRequest1();

    public void checkUserExistence() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
    }


    @Test
    void createRequestTest() {
        checkUserExistence();
        when(requestRepository.save(any())).thenReturn(request);
        ItemRequestDto newItemRequestDto = service.createRequest(user.getId(), itemRequestDto);
        itemRequestDto.setCreated(newItemRequestDto.getCreated());

        assertEquals(itemRequestDto.getDescription(), newItemRequestDto.getDescription());
        verify(requestRepository, Mockito.times(1)).save(any());
    }

    @Test
    void throwExceptionTest() {
        when(userRepository.findById(anyLong())).thenThrow(new NotFoundException("Пользователь не найден"));
        NotFoundException exception = assertThrows(NotFoundException.class, () ->
                service.getAllRequestsInfo(1L));
        assertEquals("Пользователь не найден", exception.getMessage());
    }

    @Test
    void getOneRequestInfoTest() {
        checkUserExistence();
        when(requestRepository.findById(anyLong())).thenReturn(Optional.ofNullable(request));
        item.setRequest(request);
        when(itemRepository.searchByItemRequestId(anyLong())).thenReturn(Collections.singletonList(item));
        ItemRequestResponseDto itemRequestResponseDto = service.getOneRequestInfo(user.getId(), request.getId());

        assertNotNull(itemRequestResponseDto);
        verify(requestRepository).findById(anyLong());
        verify(itemRepository).searchByItemRequestId(anyLong());
    }

    @Test
    void getAllRequestsInfoTest() {
        checkUserExistence();
        List<ItemRequestResponseDto> requestsList = service.getAllRequestsInfo(user.getId());
        assertTrue(requestsList.isEmpty());
        verify(requestRepository).findAllByRequesterId(anyLong());
    }

    @Test
    void getRequestsList() {
        when(requestRepository.findAllPageable(anyLong(), any())).thenReturn(Collections.singletonList(request));

        List<ItemRequestResponseDto> items = service.getRequestsList(1L, 0, 20);
        assertEquals(1, items.size());
        verify(requestRepository).findAllPageable(anyLong(), any());
    }
}
