package ru.practicum.shareit.booking;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;

import javax.validation.Valid;
import javax.validation.constraints.PositiveOrZero;

/**
 * TODO Sprint add-bookings.
 */
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Validated
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingController {

    private final BookingClient client;
    final String header = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> createBooking(@RequestHeader(header) long id,
                                                @Valid @RequestBody BookingDto bookingDto) {
        return client.create(id, bookingDto);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getById(@RequestHeader(header) long userId,
                                      @PathVariable long bookingId) {
        return client.getBookingInfo(userId, bookingId);
    }

    @GetMapping()
    public ResponseEntity<Object> getByBooker(@RequestHeader(header) long userId,
                                           @RequestParam(defaultValue = "ALL", required = false) String state,
                                           @PositiveOrZero @RequestParam (defaultValue = "0", required = false) int from,
                                           @PositiveOrZero @RequestParam (defaultValue = "20", required = false) int size) {
        return client.getByBooker(userId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getAllByOwner(@RequestHeader(header) long userId,
                                           @RequestParam(defaultValue = "ALL", required = false) String state,
                                           @PositiveOrZero @RequestParam (defaultValue = "0", required = false) int from,
                                           @PositiveOrZero @RequestParam (defaultValue = "20", required = false) int size) {
        return client.getByOwner(userId, state, from, size);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> update(@RequestHeader(header) long userId,
                                     @PathVariable long bookingId,
                                     @RequestParam boolean approved) {
        return client.changeStatus(userId, bookingId, approved);
    }


}
