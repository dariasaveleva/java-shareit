package ru.practicum.shareit;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.BookingItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.RequestMapper;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Setter
@Getter
public class TestHelper {

    String header = "X-Sharer-User-Id";
   //USER
    User user = new User(
            1L,
            "prince",
            "prince@mail.ru");


    User user1 = new User(null, "user1", "user@mail.ru");
    User user2 = new User(null, "user2", "user2@mail.ru");
    UserDto userDto = new UserDto (1L, "prince", "prince@mail.ru");

    //ITEM
    Item item = new Item(
            1L,
            "name",
            "description",
            true,
            user,
            null);

    Item item1 = new Item(
            null,
            "name1",
            "description1",
            true,
            user,
            null);
    ItemRequestDto itemRequestDto = new ItemRequestDto(
            1L,
            1L,
            "нужна щетка для обуви",
            LocalDateTime.now()
    );

    ItemDto itemDto = ItemMapper.toItemDto(item);

    ItemDto itemDto1 = new ItemDto(1L, "new name", "new description", true, null);
    BookingItemDto bookingItemDto = ItemMapper.toBookingItemDto(item);

    ItemRequestResponseDto itemRequestResponseDto = RequestMapper
            .toItemRequestResponseDto(RequestMapper.toRequest(itemRequestDto, user));

    //REQUEST
    Request request = new Request(null, user, "description", LocalDateTime.now());

    Request request1 = RequestMapper.toRequest(itemRequestDto, user);

    //COMMENT
    CommentDto commentDto = new CommentDto();

    Comment comment = new Comment(null, "comment", item1, user1, LocalDateTime.now());

    //Booking
    Booking booking = new Booking(1L,
            LocalDateTime.now().minusHours(2),
            LocalDateTime.now().minusHours(1),
            item,
            user,
            BookingStatus.APPROVED);
 Booking booking1 = new Booking(null,
         LocalDateTime.now().minusHours(3),
         LocalDateTime.now().minusHours(1),
         item,
         user2,
         null);

    BookingDtoResponse bookingDtoResponse = new BookingDtoResponse(
            1L,
            LocalDateTime.now().plusHours(2),
            LocalDateTime.now().plusHours(3),
            item,
            user,
            BookingStatus.WAITING
    );

    BookingDto bookingDto = new BookingDto(
            null,
            LocalDateTime.now().plusHours(2),
            LocalDateTime.now().plusHours(3),
            1L,
            1L,
            BookingStatus.WAITING
    );

    BookingDto bookingDto1 = new BookingDto(
            1L,
            LocalDateTime.now().minusHours(2),
            LocalDateTime.now().minusHours(1),
            1L,
            2L,
            null
    );
}
