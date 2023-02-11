package ru.practicum.shareit.user.Repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.Exception.NotFoundException;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Repository
public class UserRepositoryImpl implements UserRepository {

    Map<Long, User> users = new HashMap<>();
    private long userId = 1;

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public Optional<User> getUserById(long userId) {
        return Optional.ofNullable(users.get(userId));
    }

    @Override
    public UserDto createUser(UserDto userDto) {
       userDto.setId(userId++);
       users.put(userDto.getId(), UserMapper.toUser(userDto));
       return userDto;
    }

    @Override
    public UserDto updateUser(long userId, UserDto userDto) {
        if (users.containsKey(userId)) {
            if (userDto.getName() != null) users.get(userId).setName(userDto.getName());
            if (userDto.getEmail() != null) users.get(userId).setEmail(userDto.getEmail());
            return UserMapper.toUserDto(users.get(userId));
        } else throw new NotFoundException("Пользователь не существует.");
    }

    @Override
    public void delete(long userId) {
            users.remove(userId);
    }
}
