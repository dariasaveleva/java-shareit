package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.TestHelper;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.Collections;
import java.util.Optional;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    UserRepository userRepository;
    @InjectMocks
    UserServiceImpl service;
    @Captor
    ArgumentCaptor<User> userArgumentCaptor;

    TestHelper test = new TestHelper();

    User user = test.getUser();
    UserDto userDto = test.getUserDto();

    @Test
    public void findAllUsersTest() {
        when(userRepository.findAll()).thenReturn(Collections.singletonList(user));
        assertFalse(service.findAll().isEmpty());
        assertEquals(service.findAll().get(0).getName(), UserMapper.toUserDto(user).getName());
        verify(userRepository, Mockito.times(2)).findAll();
    }

    @Test
    public void getUserByIdTest() {
        UserDto user1 = new UserDto();
        when(userRepository.findById(0L)).thenReturn(Optional.of(new User()));
        UserDto currentUser = service.getUserById(0L);
        assertEquals(user1, currentUser);
    }

    @Test
    public void createUserTest() {
        when(userRepository.save(any())).thenReturn(user);
        UserDto currentUserDto = service.createUser(userDto);
        assertEquals(userDto, currentUserDto);
        verify(userRepository).save(any());
    }

    @Test
    public void updateUserTest() {
        User existedUser = new User(1L, "User", "user@mail.ru");
        UserDto userDtoUpdate = new UserDto(1L, "Updated", "update@mail.ru");
        when(userRepository.findById(1L)).thenReturn(Optional.of(existedUser));
        when(userRepository.save(existedUser)).thenReturn(UserMapper.toUser(userDtoUpdate));

        UserDto currentUserDto = service.updateUser(1L, userDtoUpdate);
        verify(userRepository).save(userArgumentCaptor.capture());
        User saved = userArgumentCaptor.getValue();

        assertEquals(currentUserDto.getId(), saved.getId());
        assertEquals(currentUserDto.getName(), saved.getName());
        assertEquals(currentUserDto.getEmail(), saved.getEmail());
    }

    @Test
    public void deleteUserTest() {
       when(userRepository.findById(1L)).thenReturn(Optional.of(new User()));
       service.delete(1L);
       verify(userRepository).delete(any());
    }

    @Test
    public void throwExceptionWhenUserNotFound() {
        when(userRepository.findById(0L)).thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class, () ->
                service.getUserById(0L));
        assertEquals("Пользователь не найден", exception.getMessage());
    }

}
