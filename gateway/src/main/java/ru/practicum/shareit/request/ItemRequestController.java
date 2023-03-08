package ru.practicum.shareit.request;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.PositiveOrZero;

/**
 * TODO Sprint add-item-requests.
 */
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Validated
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemRequestController {

    final RequestClient client;
    final String header = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> createRequest(@RequestHeader(header) long userId,
                                                @Valid @RequestBody ItemRequestDto itemRequestDto) {
        return client.createRequest(userId, itemRequestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getAllRequestsInfo(@RequestHeader(header) long userId) {
        return client.getAllRequestsInfo(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getRequestsList(@RequestHeader(header) long userId,
                                                        @PositiveOrZero @RequestParam(defaultValue = "0", required = false) int from,
                                                        @PositiveOrZero @RequestParam(defaultValue = "10", required = false) int size) {
        return client.getRequestsList(userId, from, size);
    }


    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getOneRequestInfo(@RequestHeader(header) long userId,
                                                    @PathVariable long requestId) {
        return client.getOneRequestInfo(userId, requestId);
    }

}
