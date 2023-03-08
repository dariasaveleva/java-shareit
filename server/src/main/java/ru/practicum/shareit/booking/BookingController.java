package ru.practicum.shareit.booking;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

/**
 * TODO Sprint add-bookings.
 */
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Validated
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingController {

    final BookingService bookingService;
    final String header = "X-Sharer-User-Id";

    @PostMapping
    public BookingDtoResponse createBooking(@RequestHeader(header) long id,
                                             @RequestBody BookingDto bookingDto) {
        return bookingService.create(id, bookingDto);
    }

    @GetMapping("/{bookingId}")
    public BookingDtoResponse getById(@RequestHeader(header) long userId,
                                      @PathVariable long bookingId) {
        return bookingService.getBookingInfo(userId, bookingId);
    }

    @GetMapping()
    public List<BookingDtoResponse> getByBooker(@RequestHeader(header) long userId,
                                           @RequestParam(defaultValue = "ALL", required = false) String state,
                                           @RequestParam (defaultValue = "0", required = false) int from,
                                           @RequestParam (defaultValue = "20", required = false) int size) {
        PageRequest page = PageRequest.of(from / size, size);
        return bookingService.getByBooker(userId, state, page);
    }

    @GetMapping("/owner")
    public List<BookingDtoResponse> getAllByOwner(@RequestHeader(header) long userId,
                                           @RequestParam(defaultValue = "ALL", required = false) String state,
                                           @RequestParam (defaultValue = "0", required = false) int from,
                                           @RequestParam (defaultValue = "20", required = false) int size) {
        PageRequest page = PageRequest.of(from / size, size);
        return bookingService.getByOwner(userId, state, page);
    }

    @PatchMapping("/{bookingId}")
    public BookingDtoResponse update(@RequestHeader(header) long userId,
                                     @PathVariable long bookingId,
                                     @RequestParam boolean approved) {
        return bookingService.changeStatus(userId, bookingId, approved);
    }


}
