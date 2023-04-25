package ru.yandex.practicum.filmorate.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import java.time.LocalDate;
import java.util.Set;

import static javax.validation.Validation.buildDefaultValidatorFactory;

import static org.junit.jupiter.api.Assertions.*;

public class FilmTests {

    // Создадим валидатор, для валидации полей
    Validator validator;

    @BeforeEach
    void setValidator() {
        ValidatorFactory factory = buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void allNullFieldsFilmCreationTest() {
        assertThrows(NullPointerException.class, () -> {
            new Film(
                    0,
                    null,
                    null,
                    null,
                    0,
                    null
            );
        });
    }

    @Test
    void correctFilmCreationTest() {
        Film testFilm = new Film(
                0,
                "Film name",
                "Film description",
                LocalDate.of(1999, 12, 20),
                90,
                null
        );
        Set<ConstraintViolation<Film>> violations = validator.validate(testFilm);
        assertTrue(violations.isEmpty());
        assertEquals(90, testFilm.getDuration());
    }

    @Test
    void incorrectRelesaeDateTest() {
        Film testFilm = new Film(
                0,
                "Film name",
                "Film description",
                LocalDate.of(1880, 12, 20),
                90,
                null
        );
        Set<ConstraintViolation<Film>> violations = validator.validate(testFilm);
        assertFalse(violations.isEmpty());
    }

    @Test
    void likeTest() {
        Film testFilm = new Film(
                0,
                "Film name",
                "Film description",
                LocalDate.of(1880, 12, 20),
                90,
                null
        );
        testFilm.likeFilm(1);
        assertEquals(1, testFilm.getLikes().size());
    }

    @Test
    void dislikeTest() {
        Film testFilm = new Film(
                0,
                "Film name",
                "Film description",
                LocalDate.of(1880, 12, 20),
                90,
                null
        );
        testFilm.likeFilm(1);
        assertEquals(1, testFilm.getLikes().size());
        testFilm.dislikeFilm(1);
        assertEquals(0, testFilm.getLikes().size());
    }
}
