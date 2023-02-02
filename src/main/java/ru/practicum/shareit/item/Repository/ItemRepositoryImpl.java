package ru.practicum.shareit.item.Repository;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRepositoryImpl implements ItemRepository {
    private final Map<Long, List<Item>> items = new HashMap<>();
    private long id = 1;

    @Override
    public List<ItemDto> getAll(long userId) {
        List<Item> itemsOfUser = items.get(userId);
        return itemsOfUser.stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<ItemDto> getItemById(long itemId) {
        List<Item> allItems = new ArrayList<>();
        items.forEach((user, item1) -> allItems.addAll(item1));
        return allItems.stream()
                .filter(item1 -> item1.getId() == itemId)
                .findFirst()
                .map(ItemMapper::toItemDto);
    }

    @Override
    public Optional<ItemDto> getItemForUpdate(long userId, long itemId) {
        return items.getOrDefault(userId, Collections.emptyList()).stream()
                .filter(item1 -> item1.getId() == itemId)
                .findFirst()
                .map(ItemMapper::toItemDto);
    }

    @Override
    public List<ItemDto> searchItem(String text) {
        List<Item> allItems = new ArrayList<>();
        items.forEach((userId, item1) -> allItems.addAll(items.get(userId)));
        return allItems.stream()
                .filter(item -> item.getName().toLowerCase().contains(text.toLowerCase()) ||
                item.getDescription().toLowerCase().contains(text.toLowerCase()))
                .filter(Item::getAvailable)
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto create(long userId, ItemDto itemDto) {
        itemDto.setId(id++);
        Item item = ItemMapper.toItem(itemDto, userId);
        items.compute(userId, (id, userItems) -> {
            if (userItems == null) {
                userItems = new ArrayList<>();
            }
            userItems.add(item);
            return userItems;
        });
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto update(long userId, long itemId, ItemDto itemDto) {
        Item item = items.get(userId).stream()
                .filter(item1 -> item1.getId() == itemId)
                .findFirst()
                .get();
        if (itemDto.getName() != null) item.setName(itemDto.getName());
        if (itemDto.getDescription() != null) item.setDescription(itemDto.getDescription());
        if (itemDto.getAvailable() != null) item.setAvailable(itemDto.getAvailable());
        items.get(userId).removeIf(item1 -> item1.getId() == itemId);
        items.get(userId).add(item);
        return ItemMapper.toItemDto(item);
    }
}
