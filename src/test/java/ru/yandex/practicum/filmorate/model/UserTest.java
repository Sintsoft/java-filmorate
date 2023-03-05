package ru.yandex.practicum.filmorate.model;

import ru.yandex.practicum.filmorate.model.User;

import javax.validation.ValidationException;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;

public class UserTest {

    @Test
    void userNullFieldsCreationTest() {
        Throwable thrown = assertThrows(NullPointerException.class, () -> {
            new User(0, null, null, null, null);
        });
        assertEquals(NullPointerException.class, thrown.getClass());
    }


}
