package ru.yandex.practicum.filmorate.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class FilmTest {

    @Test
    void filmNullFieldsCreationTest() {
        assertThrows(NullPointerException.class, () -> {
            new Film(0, null, null, null, null);
        });
    }

}
