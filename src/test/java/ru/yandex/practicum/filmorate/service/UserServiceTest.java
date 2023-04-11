package ru.yandex.practicum.filmorate.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.services.UserService;
import ru.yandex.practicum.filmorate.utility.exceptions.EntityValidationException;


@SpringBootTest
class UserServiceTest {

    @Autowired
    UserService service;

    @Test
    void createNullUserTest() {
        assertThrows(EntityValidationException.class, () -> service.addUser(null));
    }

    @Test
    void setFriendsTest() {
        User user = new User(0, "user", "user1", "email@mail.ru", LocalDate.of(2000, 1, 1));
        User friend = new User(0, "friend", "user1", "email@mail.ru", LocalDate.of(2000, 1, 1));
        service.addUser(user);
        service.addUser(friend);
        service.setFriends(1, 2);

        assertEquals(1, service.getUserFriends(1).size());
        assertEquals(1, service.getUserFriends(2).size());
    }

    @Test
    void unsetFriendsTest() {
        User user = new User(0, "user", "user1", "email@mail.ru", LocalDate.of(2000, 1, 1));
        User friend = new User(0, "friend", "user1", "email@mail.ru", LocalDate.of(2000, 1, 1));
        service.addUser(user);
        service.addUser(friend);
        service.setFriends(1, 2);

        assertEquals(1, service.getUserFriends(1).size());
        assertEquals(1, service.getUserFriends(2).size());

        service.unfriendUsers(1,2);

        assertEquals(0, service.getUserFriends(1).size());
        assertEquals(0, service.getUserFriends(2).size());
    }
}
