package ru.yandex.practicum.filmorate.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserTest {

    @Test
    void userNullFieldsCreationTest() {
        assertThrows(NullPointerException.class, () -> {
            new User(0, null, null, null, null);
        });
    }


}
