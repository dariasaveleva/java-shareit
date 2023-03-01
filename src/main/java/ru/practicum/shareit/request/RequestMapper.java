package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;

public class RequestMapper {

    public static ItemRequestDto toItemRequestDto (Request request) {
        return new ItemRequestDto(
                request.getId(),
            request.getRequester().getId(),
            request.getDescription(),
            request.getCreated()
        );
    }

    public static ItemRequestResponseDto toItemRequestResponseDto (Request request) {
        return new ItemRequestResponseDto(
                request.getId(),
                request.getRequester().getId(),
                request.getDescription(),
                request.getCreated(),
                new ArrayList<>()
        );
    }

    public static Request toRequest (ItemRequestDto itemRequestDto, User requester) {
        return new Request(
                itemRequestDto.getId(),
                requester,
                itemRequestDto.getDescription(),
                itemRequestDto.getCreated()
        );
    }


}
