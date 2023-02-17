package ru.practicum.shareit.booking.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.Exception.BadRequestException;
import ru.practicum.shareit.Exception.NotFoundException;
import ru.practicum.shareit.Exception.UnsupportedStatusException;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.Repository.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.Repository.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.Repository.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingServiceImpl implements BookingService{

    final UserRepository userRepository;
     final BookingRepository bookingRepository;
     final ItemRepository itemRepository;

     @Override
     @Transactional
    public BookingDtoResponse create(long id, BookingDto bookingDto) {
         User user = userRepository.findById(id).orElseThrow(() ->
                 new NotFoundException("Пользователь не существует"));
         Item item = itemRepository.findById(bookingDto.getItemId()).orElseThrow(() ->
             new NotFoundException("Объект не найден"));
         if (item.getOwner().getId() == id)
             throw new NotFoundException("Нельзя забронировать объект, которым владеете");
         if (!item.getAvailable())
             throw new BadRequestException("Объект недоступен");
         if (bookingDto.getFinishTime().isBefore(bookingDto.getStartTime()))
             throw new BadRequestException("В это время объект занят");
         bookingDto.setStatus(BookingStatus.WAITING);
         Booking booking = bookingRepository.save(BookingMapper.toBooking(bookingDto, item, user));
         BookingDtoResponse bookingDtoResponse = BookingMapper.toBookingDtoResponse(booking);
         log.info("Бронирование создано");
         return bookingDtoResponse;
     }

    @Override
    @Transactional
    public BookingDtoResponse changeStatus(long userId, long bookingId, boolean approved) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() ->
                new NotFoundException("Такого бронирования нет"));
        Item item = booking.getItem();
        if (userId != item.getOwner().getId()) throw new NotFoundException("Вы не владелец вещи");
        if (booking.getStatus() == BookingStatus.APPROVED)
            throw new BadRequestException("Нельзя поменять статус у согласованного бронирования");
        if (approved) booking.setStatus(BookingStatus.APPROVED);
        else booking.setStatus(BookingStatus.REJECTED);
        return BookingMapper.toBookingDtoResponse(bookingRepository.save(booking));
    }

    @Override
    @Transactional
    public BookingDtoResponse getBookingInfo(long userId, long bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() ->
                new NotFoundException("Такого бронирования нет"));
        Item item = booking.getItem();
        if (userId != item.getOwner().getId()) {
            if (userId != booking.getBooker().getId())
                throw new NotFoundException("Получить данные может только владелец или автор бронирования");
        }
            return BookingMapper.toBookingDtoResponse(booking);
    }

    @Override
    @Transactional
    public List<BookingDtoResponse> getByBooker(long userId, String state) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь не существует"));
        List<Booking> bookings = new ArrayList<>();
        switch (state) {
            case "ALL":
                bookings.addAll(bookingRepository.findAllByBookerIdOrderByStartTimeDesc(userId));
            case "CURRENT":
                bookings.addAll(bookingRepository.findByBookerCurrent(userId, LocalDateTime.now()));
            case "FUTURE":
                bookings.addAll(bookingRepository.findByBookerFuture(userId, LocalDateTime.now()));
            case "PAST":
                bookings.addAll(bookingRepository.findByBookerPast(userId, LocalDateTime.now()));
            case "WAITING":
                bookings.addAll(bookingRepository.findByBookerAndStatus(userId, BookingStatus.WAITING));
            case "REJECTED":
                bookings.addAll(bookingRepository.findByBookerAndStatus(userId, BookingStatus.REJECTED));
                break;
            default:
                throw new UnsupportedStatusException("Данный статус не существует");
        }
        return bookings
                .stream()
                .map(BookingMapper::toBookingDtoResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<BookingDtoResponse> getByOwner(long userId, String state) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь не существует"));
         List<Booking> bookings = new ArrayList<>();
        switch (state) {
            case "ALL":
                bookings.addAll(bookingRepository.findByItemOwnerIdOrderByStartTimeDesc(userId));
            case "CURRENT":
                bookings.addAll(bookingRepository.findByItemOwnerCurrent(userId, LocalDateTime.now()));
            case "FUTURE":
                bookings.addAll(bookingRepository.findByItemOwnerFuture(userId, LocalDateTime.now()));
            case "PAST":
                bookings.addAll(bookingRepository.findByItemOwnerPast(userId, LocalDateTime.now()));
            case "WAITING":
                bookings.addAll(bookingRepository.findByItemOwnerAndStatus(userId, BookingStatus.WAITING));
            case "REJECTED":
                bookings.addAll(bookingRepository.findByItemOwnerAndStatus(userId, BookingStatus.REJECTED));
                break;
            default:
                throw new UnsupportedStatusException("Данный статус не существует");
        }
        return bookings
                .stream()
                .map(BookingMapper::toBookingDtoResponse)
                .collect(Collectors.toList());
    }
}
