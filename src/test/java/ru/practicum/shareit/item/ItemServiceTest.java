package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.TestHelper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.comment.CommentMapper;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.repository.CommentRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.Service.ItemServiceImpl;
import ru.practicum.shareit.item.dto.BookingItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ItemServiceTest {
    @Mock
    ItemRepository itemRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    CommentRepository commentRepository;
    @Mock
    BookingRepository bookingRepository;
    @InjectMocks
    ItemServiceImpl itemService;
    @Captor
    ArgumentCaptor<Item> itemArgumentCaptor;

    TestHelper test = new TestHelper();
    @Test
    public void findAllTest() {
        PageRequest page = PageRequest.of(0, 20);
        when(itemRepository.findAllByOwnerIdOrderByIdAsc(anyLong(), any())).thenReturn(Collections.emptyList());
        assertTrue(itemService.findAll(1L, page).isEmpty());
        }

    @Test
    public void findItemTest() {
      BookingItemDto newItem = new BookingItemDto();
      newItem.setComments(new ArrayList<>());
      when(itemRepository.findById(1L)).thenReturn(Optional.of(new Item()));

      BookingItemDto currentItem = itemService.findItem(1L, 1L);
      assertEquals(newItem, currentItem);
    }

    @Test
    public void throwException() {
        when(itemRepository.findById(1L)).thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class, () ->
                itemService.findItem(1L, 1L));
        assertEquals("Объект не найден", exception.getMessage());
    }

   @Test
    public void createItem() {
        User user = test.getUser();
        Request request = test.getRequest();
        ItemDto itemDto = test.getItemDto();
        Item item = test.getItem();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRepository.save(item)).thenReturn(item);
        ItemDto currentItemDto = itemService.createItem(1L, itemDto);
        assertEquals(itemDto, currentItemDto);
        verify(itemRepository).save(item);
    }

    @Test
    public void updateItem() {
        User user = test.getUser();
        Item existedItem = test.getItem();
        ItemDto newItem = test.getItemDto1();
        when(itemRepository.findById(1L)).thenReturn(Optional.of(existedItem));
        when(itemRepository.save(existedItem)).thenReturn(ItemMapper.toItem(newItem, user, null));

        ItemDto current = itemService.updateItem(1L, 1L, newItem);

        verify(itemRepository).save(itemArgumentCaptor.capture());
        Item saved = itemArgumentCaptor.getValue();

        assertEquals(current.getId(), saved.getId());
        assertEquals(current.getName(), saved.getName());
        assertEquals(current.getDescription(), saved.getDescription());
        assertEquals(current.getAvailable(), saved.getAvailable());
        assertNull(current.getRequestId());
    }

    @Test
    public void searchByTextTest() {
        when(itemRepository.searchByText(anyString(), any())).thenReturn(Collections.emptyList());
        PageRequest page = PageRequest.of(0, 20);

        List<ItemDto> current = itemService.searchItem("дрель", page);
        assertTrue(current.isEmpty());
    }

    @Test
    public void addComment() {
        User user = test.getUser();
        Item item = test.getItem();
        Booking booking = test.getBooking();
        CommentDto commentDto = test.getCommentDto();
        commentDto.setId(1L);
        commentDto.setText("Отличная дрель для любых поверхностей");
        commentDto.setAuthorName(user.getName());

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(bookingRepository.findByBookerIdAndItemIdAndEndBefore(anyLong(), anyLong(), any())).thenReturn(Optional.of(booking));

        Comment comment = CommentMapper.toComment(user, item, commentDto);
        CommentDto current = itemService.addComment(1L, 1L, commentDto);
        comment.setCreated(current.getCreated());

        verify(commentRepository).save(comment);
        assertEquals(comment.getId(), current.getId());
        assertEquals(comment.getText(), current.getText());
        assertEquals(comment.getAuthor().getName(), current.getAuthorName());
    }
}
