package ru.practicum.shareit.item.Repository;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {
    List<ItemDto> getAll(long userId);

    Optional<ItemDto> getItemById(long itemId);

    Optional<ItemDto> getItemForUpdate(long userId, long itemId);

    List<ItemDto> searchItem(String text);

    ItemDto create(long userId, ItemDto itemDto);

    ItemDto update(long userId, long itemId, ItemDto itemDto);
}
