package ru.yandex.practicum.filmorate.model;

import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.ValidationException;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Duration;
import java.time.LocalDate;

public class FilmTest {
    
    @Test
    void filmNullFieldsCreationTest() {
        Throwable thrown = assertThrows(NullPointerException.class, () -> {
            new Film(0, null, null, null, null);
        });
    }

}
