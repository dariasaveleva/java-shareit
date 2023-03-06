package ru.practicum.shareit.booking.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.exception.UnsupportedStateException;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;

import java.util.List;

public interface BookingService {
    BookingDtoResponse create(long id, BookingDto bookingDto);

    BookingDtoResponse changeStatus(long userId, long bookingId, boolean approved);

    BookingDtoResponse getBookingInfo(long userId, long bookingId);

    List<BookingDtoResponse> getByBooker(long userId, String state, Pageable page); //add pageable

    List<BookingDtoResponse> getByOwner(long userId, String state, Pageable page) throws UnsupportedStateException; //add pageable

}
