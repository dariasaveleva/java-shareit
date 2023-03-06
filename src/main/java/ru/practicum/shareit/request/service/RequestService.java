package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;

import java.util.List;

public interface RequestService {
    ItemRequestDto createRequest(long userId, ItemRequestDto itemRequestDto);

    List<ItemRequestResponseDto> getAllRequestsInfo(long userId);

    ItemRequestResponseDto getOneRequestInfo(long userId, long requestId);

    List<ItemRequestResponseDto> getRequestsList(long userId, int from, int size);

}
