package ru.practicum.shareit.user.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.Exception.NotFoundException;
import ru.practicum.shareit.Exception.ValidationException;
import ru.practicum.shareit.user.Repository.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor_ = @Autowired)

public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public List<User> findAll() {
        log.info("Отправлен список пользователей");
        return userRepository.findAll();
    }

    @Override
    public User getUserById(long userId) {
        return userRepository.getUserById(userId).orElseThrow(() -> {
            log.warn("Пользователь не найден");
            throw new NotFoundException("Пользователь не найден");
        });
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        checkUserExistence(userDto.getEmail());
        log.info("Пользователь создан");
        return userRepository.createUser(userDto);
    }

    @Override
    public UserDto updateUser(long userId, UserDto userDto) {
        if (userDto.getEmail() != null) checkUserExistence(userDto.getEmail());
        log.info("Пользователь обновлён");
        return userRepository.updateUser(userId, userDto);
    }

    @Override
    public void delete(long userId) {
        log.info("Пользователь удален", userId);
        userRepository.delete(userId);
    }

    private void checkUserExistence(String email) {
        List<User> users = userRepository.findAll();
        boolean userExistence = users.stream()
                .anyMatch(user -> user.getEmail().equals(email));
        if (userExistence) {
            log.warn("Пользователь уже существует");
            throw new ValidationException("Пользователь уже существует");
        }
    }
}
