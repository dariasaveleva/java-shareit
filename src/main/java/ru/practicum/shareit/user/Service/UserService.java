package ru.practicum.shareit.user.Service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    List<UserDto> findAll();

    UserDto getUserById(long userId);

    UserDto createUser(UserDto userDto);

    UserDto updateUser(long userId, UserDto userDto);

    void delete(long userId);
}
