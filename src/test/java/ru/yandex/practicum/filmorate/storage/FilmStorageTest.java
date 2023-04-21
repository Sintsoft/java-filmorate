package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.utility.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.utility.exceptions.IncorrectEntityIDException;

import java.time.Duration;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public abstract class FilmStorageTest {

    FilmStorage testFilmStorage;

    protected Film getValidFilmForTest() {
        return new Film(
                0,
                "Film name",
                "Film description",
                LocalDate.of(1999, 12, 20),
                Duration.ofMinutes(90)
        );
    }

    @Test
    void addValidFilmTest() {
        Film testFilm = getValidFilmForTest();
        testFilmStorage.addFilm(testFilm);

        Film testFilmFromStorage = testFilmStorage.getFilmById(1);
        assertEquals(1, testFilmStorage.getAllFilms().size());
        assertEquals(testFilm.getId(), testFilmFromStorage.getId());
    }

    @Test
    void addInvalidFilmTest() {
        Film testFilm = getValidFilmForTest();
        testFilm.setId(-1);

        assertThrows(IncorrectEntityIDException.class, () ->  {testFilmStorage.addFilm(testFilm);});
        assertEquals(0, testFilmStorage.getAllFilms().size());
    }

    @Test
    void deleteValidFilmTest() {
        Film testFilm = getValidFilmForTest();
        testFilmStorage.addFilm(testFilm);

        assertEquals(1, testFilmStorage.getAllFilms().size());
        assertEquals(1, testFilm.getId());

        testFilmStorage.deleteFilm(testFilm);
        assertEquals(0, testFilmStorage.getAllFilms().size());
    }

    @Test
    void deleteInalidFilmTest() {
        Film testFilm = getValidFilmForTest();
        testFilmStorage.addFilm(testFilm);

        assertEquals(1, testFilmStorage.getAllFilms().size());
        assertEquals(1, testFilm.getId());

        testFilm.setId(15);

        assertThrows(Exception.class, () -> {testFilmStorage.deleteFilm(testFilm);});
        assertEquals(1, testFilmStorage.getAllFilms().size());
    }

    @Test
    void updateValidFilmTest() {
        Film testFilm = getValidFilmForTest();
        testFilmStorage.addFilm(testFilm);

        assertEquals(1, testFilmStorage.getAllFilms().size());
        assertEquals(1, testFilm.getId());

        testFilm.setName("Different name");
        testFilmStorage.updateFilm(testFilm);
        assertEquals("Different name", testFilmStorage.getFilmById(1).getName());
    }

    @Test
    void updateteInalidFilmTest() {
        Film testFilm = getValidFilmForTest();
        testFilmStorage.addFilm(testFilm);

        assertEquals(1, testFilmStorage.getAllFilms().size());
        assertEquals(1, testFilm.getId());

        testFilm.setId(15);

        assertThrows(Exception.class, () -> {testFilmStorage.updateFilm(testFilm);});
    }

}