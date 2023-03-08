package ru.practicum.shareit.request;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.service.RequestService;

import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Validated
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemRequestController {

    final RequestService requestService;
    final String header = "X-Sharer-User-Id";

    @PostMapping
    public ItemRequestDto createRequest(@RequestHeader(header) long userId,
                                        @RequestBody ItemRequestDto itemRequestDto) {
        return requestService.createRequest(userId, itemRequestDto);
    }

    @GetMapping
    public List<ItemRequestResponseDto> getAllRequestsInfo(@RequestHeader(header) long userId) {
        return requestService.getAllRequestsInfo(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestResponseDto> getRequestsList(@RequestHeader(header) long userId,
                                                         @RequestParam(defaultValue = "0", required = false) int from,
                                                         @RequestParam(defaultValue = "10", required = false) int size) {
        return requestService.getRequestsList(userId, from, size);
    }


    @GetMapping("/{requestId}")
    public ItemRequestResponseDto getOneRequestInfo(@RequestHeader(header) long userId,
                                                    @PathVariable long requestId) {
        return requestService.getOneRequestInfo(userId, requestId);
    }

}
