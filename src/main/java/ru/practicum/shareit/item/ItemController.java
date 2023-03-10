package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.Service.ItemService;
import ru.practicum.shareit.item.dto.BookingItemDto;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.Create;

import javax.validation.constraints.PositiveOrZero;
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
    public List<BookingItemDto> findAll(@RequestHeader(header) long userId,
                                        @PositiveOrZero
                                        @RequestParam (defaultValue = "0", required = false) int from,
                                        @PositiveOrZero
                                        @RequestParam (defaultValue = "20", required = false) int size) {
        PageRequest page = PageRequest.of(from / size, size);
        return service.findAll(userId, page);
    }

    @GetMapping("/{itemId}")
    public BookingItemDto findItem(@RequestHeader(header) long userId, @PathVariable long itemId) {
        return service.findItem(userId, itemId);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam String text,
                                @PositiveOrZero
                                @RequestParam (defaultValue = "0", required = false) int from,
                                @PositiveOrZero
                                @RequestParam (defaultValue = "20", required = false) int size) {
        PageRequest page = PageRequest.of(from / size, size);
        return service.searchItem(text, page);
    }

    @PostMapping
    public ItemDto createItem(@RequestHeader(header) long userId,
                              @Validated(Create.class) @RequestBody ItemDto itemDto) {
        return service.createItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader(header) long userId,
                              @PathVariable long itemId,
                              @RequestBody ItemDto itemDto) {
        return service.updateItem(userId, itemId, itemDto);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader(header) long userId,
                                 @PathVariable long itemId,
                                 @Validated(Create.class) @RequestBody CommentDto commentDto) {
        return service.addComment(userId, itemId, commentDto);
    }
}
