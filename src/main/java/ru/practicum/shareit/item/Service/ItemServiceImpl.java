package ru.practicum.shareit.item.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.comment.CommentMapper;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.comment.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.dto.BookingItemDto;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor(onConstructor_ = @Autowired)

public class ItemServiceImpl implements ItemService {
     private final CommentRepository commentRepository;
     private final ItemRepository itemRepository;
     private final UserRepository userRepository;
     private final BookingRepository bookingRepository;
     private final RequestRepository requestRepository;

    @Override
    @Transactional
    public ItemDto createItem(long userId, ItemDto itemDto) {
        User user = userRepository.findById(userId).orElseThrow(() -> {
            log.warn("Пользователь не существует");
            throw new NotFoundException("Пользователь не существует");
        });
        Request request = null;
        if (itemDto.getRequestId() != null) {
            request = requestRepository.findById(itemDto.getRequestId()).orElseThrow(() ->
            new NotFoundException("Запрос не найден"));
        }
        log.info("Создан новый объект");
        Item item = itemRepository.save(ItemMapper.toItem(itemDto, user, request));
        itemDto.setId(item.getId());
        return itemDto;
    }

    @Override
    @Transactional
    public ItemDto updateItem(long userId, long itemId, ItemDto itemDto) {
       Item item = itemRepository.findById(itemId).orElseThrow(() -> {
            throw new NotFoundException("Объект не найден");
        });
       if (item.getOwner().getId() == userId) {
           if (itemDto.getName() != null) item.setName(itemDto.getName());
           if (itemDto.getDescription() != null) item.setDescription(itemDto.getDescription());
           if (itemDto.getAvailable() != null) item.setAvailable(itemDto.getAvailable());
           itemRepository.save(item);
           log.info("Объект обновлён");
       } else {
           throw new NotFoundException("Объект не найден");
       }
       return ItemMapper.toItemDto(item);
    }

    @Override
    public BookingItemDto findItem(long userId, long itemId) {
        log.info("Найден объект");
        Item item = itemRepository.findById(itemId).orElseThrow(() -> {
            throw new NotFoundException("Объект не найден");
        });
        return collectItemsWithBookingAndComments(userId, Collections.singletonList(item)).get(0);
    }

    @Override
    public List<BookingItemDto> findAll(long userId, Pageable page) {
        log.info("Найдены объекты");
        return  collectItemsWithBookingAndComments(userId,
                itemRepository.findAllByOwnerIdOrderByIdAsc(userId, page));
    }

    @Override
    public List<ItemDto> searchItem(String text, Pageable page) {
        if (text.isBlank()) return Collections.emptyList();
        log.info("Найдены объекты по тексту");
        return itemRepository.searchByText(text.toLowerCase(), page)
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CommentDto addComment(long userId, long itemId, CommentDto commentDto) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь не найден"));
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new NotFoundException("Объект не найден"));
        bookingRepository.findByBookerIdAndItemIdAndEndBefore(userId, itemId, LocalDateTime.now())
                .orElseThrow(() -> new BadRequestException("Невозможно добавить коммент"));
        commentDto.setCreated(LocalDateTime.now());
        Comment comment = CommentMapper.toComment(user, item, commentDto);
        commentRepository.save(comment);
        return CommentMapper.toCommentDto(comment);
        }

    private List<BookingItemDto> collectItemsWithBookingAndComments(long userId, List<Item> items) {
        List<Long> ids = items.stream()
                .map(Item::getId)
                .collect(Collectors.toList());
        List<Booking> bookings = bookingRepository.findBookingsLast(ids, LocalDateTime.now(), userId);
        Map<Long, BookingItemDto> itemsMap = items.stream()
                .map(ItemMapper::toBookingItemDto)
                .collect(Collectors.toMap(BookingItemDto::getId, item -> item, (a, b) -> b));
        bookings.forEach(booking -> itemsMap.get(booking.getItem().getId())
                .setLastBooking(BookingMapper.toBookingDto(booking)));
        bookings = bookingRepository.findBookingsNext(ids, LocalDateTime.now(), userId);
        bookings.forEach(booking -> itemsMap.get(booking.getItem().getId())
                .setNextBooking(BookingMapper.toBookingDto(booking)));
        List<Comment> comments = commentRepository.findAllByComments(ids);
        comments.forEach(comment -> itemsMap.get(comment.getItem().getId())
                .getComments().add(CommentMapper.toCommentDto(comment)));
        return new ArrayList<>(itemsMap.values());
    }
    }
