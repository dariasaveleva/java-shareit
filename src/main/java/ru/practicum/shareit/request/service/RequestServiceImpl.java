package ru.practicum.shareit.request.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.RequestMapper;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RequestServiceImpl implements RequestService {
    final ItemRepository itemRepository;
    final RequestRepository requestRepository;
    final UserRepository userRepository;

    @Override
    public ItemRequestDto createRequest(long userId, ItemRequestDto itemRequestDto) {
        User user = checkUserExistence(userId);
        itemRequestDto.setCreated(LocalDateTime.now());
        Request request = requestRepository.save(RequestMapper.toRequest(itemRequestDto, user));
        log.info("Запрос создан");
        return RequestMapper.toItemRequestDto(request);
    }

    @Override
    public List<ItemRequestResponseDto> getAllRequestsInfo(long userId) {
        checkUserExistence(userId);
        List<ItemRequestResponseDto> requests = requestRepository.findAllByRequesterId(userId).stream()
                .map(RequestMapper::toItemRequestResponseDto)
                .collect(Collectors.toList());
        return linkItemsToRequests(requests);
    }

    @Override
    public ItemRequestResponseDto getOneRequestInfo(long userId, long requestId) {
        checkUserExistence(userId);
        Request request = requestRepository.findById(requestId).orElseThrow(() ->
                new NotFoundException("Запрос не существует"));
        List<ItemDto> items = itemRepository.searchByItemRequestId(requestId).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
        ItemRequestResponseDto requestResponseDto = RequestMapper.toItemRequestResponseDto(request);
        requestResponseDto.setItems(items);
        return requestResponseDto;
    }

    @Override
    public List<ItemRequestResponseDto> getRequestsList(long userId, int from, int size) {
        int page = from / size;
        PageRequest pageRequest = PageRequest.of(page, size);
        List<ItemRequestResponseDto> responseDtos = requestRepository.findAllPageable(userId, pageRequest)
                .stream()
                .map(RequestMapper::toItemRequestResponseDto)
                .collect(Collectors.toList());
        return linkItemsToRequests(responseDtos);
    }

    private List<ItemRequestResponseDto> linkItemsToRequests(List<ItemRequestResponseDto> requestsResponseDto) {
        Map<Long, ItemRequestResponseDto> requests = requestsResponseDto.stream()
                .collect(Collectors.toMap(ItemRequestResponseDto::getId, r -> r, (a,b) -> b));
        List<Long> ids = requests.values().stream()
                .map(ItemRequestResponseDto::getId)
                .collect(Collectors.toList());
        List<ItemDto> items = itemRepository.searchByRequestId(ids).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
        items.forEach(itemDto -> requests.get(itemDto.getRequestId()).getItems().add(itemDto));
        return new ArrayList<>(requests.values());
    }

    private User checkUserExistence(long userId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь не существует"));
        return user;
    }
}
