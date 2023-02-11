package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.Service.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.Create;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemController {

    private final ItemService service;
    private final String header = "X-Sharer-User-Id";

    @GetMapping
    public List<ItemDto> findAll(@RequestHeader(header) long userId) {
        return service.getAllItemsByUser(userId);
    }

    @GetMapping("/{itemId}")
    public ItemDto findItem(@PathVariable long itemId) {
        return service.getItemById(itemId);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam String text) {
        return service.searchItem(text);
    }

    @PostMapping()
    public ItemDto createItem(@RequestHeader(header) long userId, @Validated(Create.class) @RequestBody ItemDto itemDto) {
        return service.createItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader(header) long userId, @PathVariable long itemId,@RequestBody ItemDto itemDto) {
        return service.updateItem(userId, itemId, itemDto);
    }
}
