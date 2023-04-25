package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.utility.exceptions.IncorrectEntityIDException;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@EnableConfigurationProperties
@SqlGroup({
        @Sql(scripts = {"classpath:schema.sql"},
                executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
})
public abstract class FilmStorageTest {

    FilmStorage testFilmStorage;
    UserStorage testUserStorage;

    protected Film getValidFilmForTest() {
        return new Film(
                0,
                "Film name",
                "Film description",
                LocalDate.of(1999, 12, 20),
                90,
                new MPA(1)
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

        assertThrows(IncorrectEntityIDException.class, () ->  {
            testFilmStorage.addFilm(testFilm);
        });
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

        assertThrows(Exception.class, () -> {
            testFilmStorage.deleteFilm(testFilm);
        });
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

        assertThrows(Exception.class, () -> {
            testFilmStorage.updateFilm(testFilm);
        });
    }

    @Test
    void getMostLikedFilm() {
        Film testFilm = getValidFilmForTest();
        User testUser = new User(
                0,
                "User Name",
                "Login",
                "email@yandex.ru",
                LocalDate.of(2000,1,1)
        );

        testFilmStorage.addFilm(testFilm);
        testUserStorage.addUser(testUser);

        assertEquals(1, testFilm.getId());
        assertEquals(1, testUser.getId());

        testFilmStorage.saveLike(1, 1);
        assertEquals(1, testFilmStorage.getMostLikedFilms(10).size());
    }


}
