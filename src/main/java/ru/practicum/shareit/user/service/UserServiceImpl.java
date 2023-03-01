package ru.practicum.shareit.user.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Transactional(readOnly = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserServiceImpl implements UserService {
     final UserRepository userRepository;

    @Override
    public List<UserDto> findAll() {
        log.info("Отправлен список пользователей");
        return userRepository.findAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getUserById(long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> {
                log.warn("Пользователь не найден");
        throw new NotFoundException("Пользователь не найден");
        });
        return UserMapper.toUserDto(user);
    }

    @Override
    @Transactional
    public UserDto createUser(UserDto userDto) {
        log.info("Пользователь создан");
        User user = userRepository.save(UserMapper.toUser(userDto));
        return UserMapper.toUserDto(user);
    }

    @Override
    @Transactional
    public UserDto updateUser(long userId, UserDto userDto) {
        User user = userRepository.findById(userId).orElseThrow(() -> {
            log.warn("Пользователь не найден");
            throw new NotFoundException("Пользователь не найден");
        });
        if (userDto.getEmail() != null) user.setEmail(userDto.getEmail());
        if (userDto.getName() != null) user.setName(userDto.getName());
        log.info("Пользователь обновлён");
        return UserMapper.toUserDto(userRepository.save(user));
    }

    @Override
    @Transactional
    public void delete(long userId) {
        log.info("Пользователь удален", userId);
        userRepository.findById(userId).ifPresent(userRepository::delete);
    }
}
