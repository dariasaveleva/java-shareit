package ru.practicum.shareit.booking.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.user.Create;
import ru.practicum.shareit.user.Update;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingDto {
    @NotNull(groups = Update.class)
    long id;
    @FutureOrPresent(groups = Create.class)
    LocalDateTime startTime;
    @Future(groups = Create.class)
    LocalDateTime finishTime;
    @NotNull(groups = Create.class)
    long itemId;
    long bookerId;
    BookingStatus status;
}
