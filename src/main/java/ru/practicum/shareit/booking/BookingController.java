package ru.practicum.shareit.booking;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.Service.ItemService;
import ru.practicum.shareit.user.Create;
import ru.practicum.shareit.user.Service.UserService;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-bookings.
 */
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingController {

    final BookingService bookingService;
    final String header = "X-Sharer-User-Id";

    @PostMapping
    public BookingDtoResponse createBoooking(@RequestHeader(header) long id,
                                             @Validated(Create.class) @RequestBody BookingDto bookingDto) {
        return bookingService.create(id, bookingDto);
    }

    @GetMapping("/{bookingId}")
    public BookingDtoResponse getById(@RequestHeader(header) long userId,
                                      @PathVariable long bookingId) {
        return bookingService.getBookingInfo(userId, bookingId);
    }

    @GetMapping()
    public List<BookingDtoResponse> getAllByBooker(@RequestHeader(header) long userId,
                                           @RequestParam(defaultValue = "ALL", required = false) String state) {
        return bookingService.getByBooker(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingDtoResponse> getAllByOwner(@RequestHeader(header) long userId,
                                           @RequestParam(defaultValue = "ALL", required = false) String state) {
        return bookingService.getByOwner(userId, state);
    }

    @PatchMapping("/{bookingId}")
    public BookingDtoResponse update(@RequestHeader(header) long userId,
                                     @PathVariable long bookingId,
                                     @RequestParam boolean approved) {
        return bookingService.changeStatus(userId, bookingId, approved);
    }


}
