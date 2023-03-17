package ru.yandex.practicum.filmorate.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import ru.yandex.practicum.filmorate.model.User;

public abstract class UserStorageTest {
    
    UserStorage storage;

    @Test
    void addUserTest() {
        User addedUser = new User(
            1,
            "User name",
            "Login",
            "mail@mail.org",
            LocalDate.of(2000, 1, 1)
        );
        storage.addUser(addedUser);
        assertEquals(1, storage.getAllUsers().size());
        assertEquals(addedUser , storage.getUser(1));
    }

    @Test
    void addNullUserTest() {
        assertThrows(NullPointerException.class, () -> {
            storage.addUser(null);
        });
    }

    @Test
    void addWrongIdUserTest() {
        User addedUser = new User(
            -2,
            "User name",
            "Login",
            "mail@mail.org",
            LocalDate.of(2000, 1, 1)
        );
        storage.addUser(addedUser);
        assertEquals(0, storage.getAllUsers().size());
        assertNull(storage.getUser(-2));
    }

    @Test
    void deleteUserTest() {
        User addedUser = new User(
            1,
            "User name",
            "Login",
            "mail@mail.org",
            LocalDate.of(2000, 1, 1)
        );
        storage.addUser(addedUser);
        assertEquals(1, storage.getAllUsers().size());
        assertEquals(addedUser , storage.getUser(1));
        storage.deleteUser(addedUser);
        assertEquals(0, storage.getAllUsers().size());
        assertNull(storage.getUser(1));
    }
}
