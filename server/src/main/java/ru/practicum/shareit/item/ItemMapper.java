package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.BookingItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;

public class ItemMapper {

    public static ItemDto toItemDto(Item item) {
        Long requestId = null;
        if (item.getRequest() != null) requestId = item.getRequest().getId();
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                requestId
        );
    }

    public static Item toItem(ItemDto itemDto, User user, Request request) {
        return new Item(
                itemDto.getId(),
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                user,
                request);
    }

    public static BookingItemDto toBookingItemDto(Item item) {
        return new BookingItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                null,
                null,
                new ArrayList<>());
    }
}
