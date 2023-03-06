package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.*;

public class UserModelTest {

    @Test
    public void testEqualsUser() {
        User user = new User(1L, "name", "user@mail.ru");
        User user1 = new User(2L, "name2", "user1@mail.ru");
        assertNotEquals(user, user1);
        assertNotEquals(user, null);
    }
}
