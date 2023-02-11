package ru.practicum.shareit.item.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.Exception.NotFoundException;
import ru.practicum.shareit.item.Repository.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.Repository.UserRepository;

import java.util.Collections;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public ItemDto createItem(long userId, ItemDto itemDto) {
        userRepository.getUserById(userId).orElseThrow(() -> {
            log.warn("Пользователь не существует");
            throw new NotFoundException("Пользователь не существует");
        });
        log.info("Создан новый объект");
        return itemRepository.create(userId, itemDto);
    }

    @Override
    public ItemDto updateItem(long userId, long itemId, ItemDto itemDto) {
       itemRepository.getItemForUpdate(userId, itemId).orElseThrow(() -> {
            log.warn("Объект не существует");
            throw new NotFoundException("Объект не существует");
        });
       log.info("Объект обновлён ");
       return itemRepository.update(userId, itemId, itemDto);
    }

    @Override
    public ItemDto getItemById(long itemId) {
        log.info("Найден объект");
        return itemRepository.getItemById(itemId).orElseThrow(() -> {
            log.warn("Объект не найден");
            throw new NotFoundException("Объект не найден");
        });
    }

    @Override
    public List<ItemDto> getAllItemsByUser(long userId) {
        userRepository.getUserById(userId).orElseThrow(() -> {
            log.warn("Пользователь не существует");
            throw new NotFoundException("Пользователь не существует");
        });
        log.info("Найдены объекты");
        return itemRepository.getAll(userId);
    }

    @Override
    public List<ItemDto> searchItem(String text) {
        if (text.isBlank()) return Collections.emptyList();
        log.info("Найдены объекты по тексту");
        return itemRepository.searchItem(text);
    }

}
