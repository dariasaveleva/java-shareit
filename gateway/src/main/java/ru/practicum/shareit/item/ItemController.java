package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Create;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.constraints.PositiveOrZero;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemController {

    private final ItemClient client;
    private final String header = "X-Sharer-User-Id";

    @GetMapping
    public ResponseEntity<Object> findAll(@RequestHeader(header) long userId,
                                          @PositiveOrZero
                                        @RequestParam (defaultValue = "0", required = false) int from,
                                          @PositiveOrZero
                                        @RequestParam (defaultValue = "20", required = false) int size) {
        return client.findAll(userId, from, size);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> findItem(@RequestHeader(header) long userId, @PathVariable long itemId) {
        return client.findItem(userId, itemId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(@RequestHeader(header) long userId,
                                         @RequestParam String text,
                                @PositiveOrZero
                                @RequestParam (defaultValue = "0", required = false) int from,
                                @PositiveOrZero
                                @RequestParam (defaultValue = "20", required = false) int size) {
        return client.searchItem(text, userId, from, size);
    }

    @PostMapping
    public ResponseEntity<Object> createItem(@RequestHeader(header) long userId,
                              @Validated(Create.class) @RequestBody ItemDto itemDto) {
        return client.createItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader(header) long userId,
                              @PathVariable long itemId,
                              @RequestBody ItemDto itemDto) {
        return client.updateItem(userId, itemId, itemDto);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader(header) long userId,
                                 @PathVariable long itemId,
                                 @Validated(Create.class) @RequestBody CommentDto commentDto) {
        return client.addComment(userId, itemId, commentDto);
    }
}
