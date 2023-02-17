package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

public class BookingMapper {

    public static BookingDto toBookingDto(Booking booking) {
        return new BookingDto(
                booking.getId(),
                booking.getStartTime(),
                booking.getFinishTime(),
                booking.getItem().getId(),
                booking.getBooker().getId(),
                booking.getStatus()
        );
    }

    public static Booking toBooking(BookingDto bookingDto, Item item, User user) {
        return new Booking(
                bookingDto.getId(),
                bookingDto.getStartTime(),
                bookingDto.getFinishTime(),
                item,
                user,
                bookingDto.getStatus()
        );
    }

    public static BookingDtoResponse toBookingDtoResponse(Booking booking) {
        return new BookingDtoResponse(
                booking.getId(),
                booking.getStartTime(),
                booking.getFinishTime(),
                booking.getItem(),
                booking.getBooker(),
                booking.getStatus()
        );
    }
}
