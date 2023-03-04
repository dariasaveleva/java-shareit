package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.TestHelper;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UnsupportedStateException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {
    @Mock
    BookingRepository bookingRepository;
    @Mock
    ItemRepository itemRepository;
    @Mock
    UserRepository userRepository;
    @InjectMocks
    BookingServiceImpl service;

    TestHelper test = new TestHelper();
    User user = test.getUser();
    User user2 = new User(2L, "second", "seconduser@mail.ru");
    Item item = test.getItem();
    BookingDto bookingDto = test.getBookingDto1();
    PageRequest page = PageRequest.of(0, 10);


    @Test
    public void createTest() {
        Booking booking = BookingMapper.toBooking(bookingDto, item, user);
        BookingDto dto = BookingMapper.toBookingDto(booking);
        BookingDtoResponse bookingDtoResponse = BookingMapper.toBookingDtoResponse(booking);

        when(itemRepository.findById(anyLong())).thenReturn(Optional.ofNullable(item));
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user2));
        when(bookingRepository.save(any())).thenReturn(booking);

        BookingDtoResponse current = service.create(2L, bookingDto);
        assertEquals(bookingDtoResponse, current);
        assertEquals(dto.getId(), bookingDto.getId());
        verify(bookingRepository).save(any());
    }

    @Test
    public void createBookingByOwnerOfItem() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.ofNullable(item));
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));

        NotFoundException exception =  assertThrows(NotFoundException.class,
                () -> service.create(1L, bookingDto));
        assertEquals("Нельзя забронировать объект, которым владеете", exception.getMessage());
    }

    @Test
    public void createBooking_IfItemNotAvailable() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.ofNullable(item));
        item.setAvailable(false);
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));

        BadRequestException exception =  assertThrows(BadRequestException.class,
                () -> service.create(2L, bookingDto));
        assertEquals("Объект недоступен", exception.getMessage());
    }

    @Test
    public void throwException_IfItemNotFound() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> service.create(1L, bookingDto));
        assertEquals("Объект не найден", exception.getMessage());
    }

    @Test
    public void throwException_IfUserNotFound() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.ofNullable(item));
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> service.create(1L, bookingDto));
        assertEquals("Пользователь не существует", exception.getMessage());
    }

    @Test
    public void throwException_IfEndBeforeStart() {
        Item item1 = item;
        item1.setId(5L);
        bookingDto.setItemId(5L);
        bookingDto.setEnd(LocalDateTime.now().minusDays(2));

        when(itemRepository.findById(anyLong())).thenReturn(Optional.ofNullable(item));
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user2));

        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> service.create(4L, bookingDto));
        assertEquals("В это время объект занят", exception.getMessage());
    }

    @Test
    public void throwException_IfOwnerRejectBooking() {
        Booking booking = BookingMapper.toBooking(bookingDto, item, user);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any())).thenReturn(booking);

        BookingDtoResponse bookingDtoResponse = service.changeStatus(user.getId(), booking.getId(), false);
        assertEquals(BookingStatus.REJECTED, bookingDtoResponse.getStatus());
        verify(bookingRepository).save(any());

    }

    @Test
    public void throwException_IfBookingNotFound() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> service.changeStatus(1L, 1L, true));
        assertEquals("Такого бронирования нет", exception.getMessage());
    }

    @Test
    public void throwException_IfChangeStatusNotByOwner() {
        Booking booking = BookingMapper.toBooking(bookingDto, item, user);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> service.changeStatus(2L, 1L, true));
        assertEquals("Вы не владелец вещи", exception.getMessage());
    }

    @Test
    public void throwException_ChangeStatus_IfBookingApproved() {
        Booking booking = BookingMapper.toBooking(bookingDto, item, user);
        booking.setStatus(BookingStatus.APPROVED);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> service.changeStatus(1L, 1L, false));
        assertEquals("Нельзя поменять статус у согласованного бронирования",
                exception.getMessage());
    }

    @Test
    public void getByIdIfUserNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> service.getByBooker(1L, "ALL", page));
        assertEquals("Пользователь не существует", exception.getMessage());
    }

    @Test
    public void getByBookerAllState() {
        Booking booking = BookingMapper.toBooking(bookingDto, item, user);
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(bookingRepository.findAllByBookerIdOrderByStartDesc(anyLong(), any()))
                .thenReturn(Collections.singletonList(booking));

        List<BookingDtoResponse> bookingDtoResponseList = service.getByBooker(user.getId(), "ALL", page);
        assertFalse(bookingDtoResponseList.isEmpty());
        assertEquals(booking.getItem().getName(), bookingDtoResponseList.get(0).getItem().getName());
        verify(bookingRepository, times(1))
                .findAllByBookerIdOrderByStartDesc(anyLong(), any());

    }

    @Test
    public void getByBookerCurrentState() {
        Booking booking = BookingMapper.toBooking(bookingDto, item, user);
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(bookingRepository.findByBookerCurrent(anyLong(), any(), any()))
                .thenReturn(Collections.singletonList(booking));

        List<BookingDtoResponse> bookingDtoResponseList = service.getByBooker(user.getId(), "CURRENT", page);
        assertFalse(bookingDtoResponseList.isEmpty());
        assertEquals(booking.getItem().getName(), bookingDtoResponseList.get(0).getItem().getName());
        verify(bookingRepository, times(1))
                .findByBookerCurrent(anyLong(), any(), any());
    }

    @Test
    public void getByBookerPastState() {
        Booking booking = BookingMapper.toBooking(bookingDto, item, user);
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(bookingRepository.findByBookerPast(anyLong(), any(), any()))
                .thenReturn(Collections.singletonList(booking));

        List<BookingDtoResponse> bookingDtoResponseList = service.getByBooker(user.getId(), "PAST", page);
        assertFalse(bookingDtoResponseList.isEmpty());
        assertEquals(booking.getItem().getName(), bookingDtoResponseList.get(0).getItem().getName());
        verify(bookingRepository, times(1))
                .findByBookerPast(anyLong(), any(), any());
    }

    @Test
    public void getByBookerFutureState() {
        Booking booking = BookingMapper.toBooking(bookingDto, item, user);
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(bookingRepository.findByBookerFuture(anyLong(), any(), any()))
                .thenReturn(Collections.singletonList(booking));

        List<BookingDtoResponse> bookingDtoResponseList = service.getByBooker(user.getId(), "FUTURE", page);
        assertFalse(bookingDtoResponseList.isEmpty());
        assertEquals(booking.getItem().getName(), bookingDtoResponseList.get(0).getItem().getName());
        verify(bookingRepository, times(1))
                .findByBookerFuture(anyLong(), any(), any());
    }

    @Test
    public void getByBookerWaitingStatus() {
        Booking booking = BookingMapper.toBooking(bookingDto, item, user);
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(bookingRepository.findByBookerAndStatus(anyLong(), any(), any()))
                .thenReturn(Collections.singletonList(booking));

        PageRequest page = PageRequest.of(0, 10);

        List<BookingDtoResponse> bookingDtoResponseList = service.getByBooker(user.getId(), "WAITING", page);
        assertFalse(bookingDtoResponseList.isEmpty());
        assertEquals(booking.getItem().getName(), bookingDtoResponseList.get(0).getItem().getName());
        verify(bookingRepository, times(1))
                .findByBookerAndStatus(anyLong(), any(), any());
    }

    @Test
    public void getByBookerRejectedStatus() {
        Booking booking = BookingMapper.toBooking(bookingDto, item, user);
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(bookingRepository.findByBookerAndStatus(anyLong(), any(), any()))
                .thenReturn(Collections.singletonList(booking));

        PageRequest page = PageRequest.of(0, 10);

        List<BookingDtoResponse> bookingDtoResponseList = service.getByBooker(user.getId(), "REJECTED", page);
        assertFalse(bookingDtoResponseList.isEmpty());
        assertEquals(booking.getItem().getName(), bookingDtoResponseList.get(0).getItem().getName());
        verify(bookingRepository, times(1))
                .findByBookerAndStatus(anyLong(), any(), any());
    }

    @Test
    public void getByBookerIfUnsupportedStatus() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));

        UnsupportedStateException exception =  assertThrows(UnsupportedStateException.class,
                () -> service.getByBooker(user.getId(), "unsupported", page));
        assertEquals("Unknown state: unsupported", exception.getMessage());
    }


    @Test
    public void getByOwnerAllState() {
        Booking booking = BookingMapper.toBooking(bookingDto, item, user);
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(bookingRepository.findByItemOwnerIdOrderByStartDesc(anyLong(), any()))
                .thenReturn(Collections.singletonList(booking));
        PageRequest page = PageRequest.of(0, 10);

        List<BookingDtoResponse> bookingDtoResponseList = service.getByOwner(user.getId(), "ALL", page);
        assertFalse(bookingDtoResponseList.isEmpty());
        assertEquals(booking.getItem().getName(), bookingDtoResponseList.get(0).getItem().getName());
        verify(bookingRepository, times(1))
                .findByItemOwnerIdOrderByStartDesc(anyLong(), any());
    }

    @Test
    public void getByOwnerCurrentState() {
        Booking booking = BookingMapper.toBooking(bookingDto, item, user);
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(bookingRepository.findByItemOwnerCurrent(anyLong(), any(), any()))
                .thenReturn(Collections.singletonList(booking));

        List<BookingDtoResponse> bookingDtoResponseList = service.getByOwner(user.getId(), "CURRENT", page);
        assertFalse(bookingDtoResponseList.isEmpty());
        assertEquals(booking.getItem().getName(), bookingDtoResponseList.get(0).getItem().getName());
        verify(bookingRepository, times(1))
                .findByItemOwnerCurrent(anyLong(), any(), any());
    }

    @Test
    public void getByOwnerPastState() {
        Booking booking = BookingMapper.toBooking(bookingDto, item, user);
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(bookingRepository.findByItemOwnerPast(anyLong(), any(), any()))
                .thenReturn(Collections.singletonList(booking));
        PageRequest page = PageRequest.of(0, 10);

        List<BookingDtoResponse> bookingDtoResponseList = service.getByOwner(user.getId(), "PAST", page);
        assertFalse(bookingDtoResponseList.isEmpty());
        assertEquals(booking.getItem().getName(), bookingDtoResponseList.get(0).getItem().getName());
        verify(bookingRepository, times(1))
                .findByItemOwnerPast(anyLong(), any(), any());
    }

    @Test
    public void getByOwnerFutureState() {
        Booking booking = BookingMapper.toBooking(bookingDto, item, user);
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(bookingRepository.findByItemOwnerFuture(anyLong(), any(), any()))
                .thenReturn(Collections.singletonList(booking));
        PageRequest page = PageRequest.of(0, 10);

        List<BookingDtoResponse> bookingDtoResponseList = service.getByOwner(user.getId(), "FUTURE", page);
        assertFalse(bookingDtoResponseList.isEmpty());
        assertEquals(booking.getItem().getName(), bookingDtoResponseList.get(0).getItem().getName());
        verify(bookingRepository, times(1))
                .findByItemOwnerFuture(anyLong(), any(), any());
    }

    @Test
    public void getByOwnerWaitingStatus() {
        Booking booking = BookingMapper.toBooking(bookingDto, item, user);
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(bookingRepository.findByItemOwnerAndStatus(anyLong(), any(), any()))
                .thenReturn(Collections.singletonList(booking));
        PageRequest page = PageRequest.of(0, 10);

        List<BookingDtoResponse> bookingDtoResponseList = service.getByOwner(user.getId(), "WAITING", page);
        assertFalse(bookingDtoResponseList.isEmpty());
        assertEquals(booking.getItem().getName(), bookingDtoResponseList.get(0).getItem().getName());
        verify(bookingRepository, times(1))
                .findByItemOwnerAndStatus(anyLong(), any(), any());
    }

    @Test
    public void getByOwnerRejectedStatus() {
        Booking booking = BookingMapper.toBooking(bookingDto, item, user);
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(bookingRepository.findByItemOwnerAndStatus(anyLong(), any(), any()))
                .thenReturn(Collections.singletonList(booking));
        PageRequest page = PageRequest.of(0, 10);

        List<BookingDtoResponse> bookingDtoResponseList = service.getByOwner(user.getId(), "REJECTED", page);
        assertFalse(bookingDtoResponseList.isEmpty());
        assertEquals(booking.getItem().getName(), bookingDtoResponseList.get(0).getItem().getName());
        verify(bookingRepository, times(1))
                .findByItemOwnerAndStatus(anyLong(), any(), any());
    }

    @Test
    public void getByOwnerIfUnsupportedStatus() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));

        UnsupportedStateException exception =  assertThrows(UnsupportedStateException.class,
                () -> service.getByOwner(user.getId(), "unsupported", page));
        assertEquals("Unknown state: unsupported", exception.getMessage());
    }

    @Test
    public void getByOwnerIfUserNotExist() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundException exception =  assertThrows(NotFoundException.class,
                () -> service.getByOwner(user.getId(), "ALL", page));
        assertEquals("Пользователь не существует", exception.getMessage());
    }

    @Test
    public void getBookingInfoTest() {
        Booking booking = BookingMapper.toBooking(bookingDto,item, user);
        when(bookingRepository.findById(bookingDto.getId()))
                .thenReturn(Optional.of(booking));

        BookingDtoResponse bookingDtoResponse = service.getBookingInfo(user.getId(), item.getId());
        assertEquals(bookingDtoResponse.getItem(), booking.getItem());
        assertEquals(bookingDtoResponse.getId(), booking.getId());
        assertEquals(bookingDtoResponse.getStatus(), booking.getStatus());
    }

    @Test
    public void getBookingInfoNotByOwner() {
        Booking booking = BookingMapper.toBooking(bookingDto,item, user);
        User user1 = new User(100L, "user1", "user1@mail.ru");
        when(bookingRepository.findById(bookingDto.getId())).thenReturn(Optional.of(booking));

        NotFoundException exception =  assertThrows(NotFoundException.class,
                () -> service.getBookingInfo(user1.getId(), booking.getId()));
        assertEquals("Получить данные может только владелец или автор бронирования",
                exception.getMessage());
    }

    @Test
    public void getBookingInfoWhenBookingNotExist() {
        Booking booking = BookingMapper.toBooking(bookingDto,item, user);
        when(bookingRepository.findById(bookingDto.getId())).thenReturn(Optional.empty());

        NotFoundException exception =  assertThrows(NotFoundException.class,
                () -> service.getBookingInfo(user.getId(), booking.getId()));
        assertEquals("Такого бронирования нет", exception.getMessage());
    }
}
