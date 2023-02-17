package ru.practicum.shareit.item.Service;

import ru.practicum.shareit.item.dto.BookingItemDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto createItem(long userId,ItemDto itemDto);

    ItemDto updateItem(long userId, long itemId, ItemDto itemDto);

    BookingItemDto findItem(long userId, long itemId);

    List<BookingItemDto> findAll(long userId);

    List<ItemDto> searchItem(String text);

    CommentDto addComment(long userId, long itemId, CommentDto commentDto);
}
