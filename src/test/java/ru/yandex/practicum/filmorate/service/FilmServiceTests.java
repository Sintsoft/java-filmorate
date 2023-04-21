package ru.yandex.practicum.filmorate.service;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.MPA;


import java.time.Duration;
import java.time.LocalDate;

@SpringBootTest
@SqlGroup(
        {
                @Sql(scripts = {"classpath:schema.sql"},
                        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
        }
)
public class FilmServiceTests {

    @Autowired
    FilmService service;

    @Autowired
    UserService userService;

    Film createTestFilm() {
        return new Film(0, "Test film", "Test film description", LocalDate.of(2022,2,2), 90, new MPA(1), null);
    }

    User createTestUser() {
        return new User(0, "name", "login", "mail@mail.ru", LocalDate.of(2000, 1, 1));
    }

    @Test
    @SneakyThrows
    void addLikeToFilmTest() {
        service.addFilm(createTestFilm());
        userService.addUser(createTestUser());
        service.likeFilm(1,1);
        assertEquals(1, service.getFilm(1).getLikes().size());
    }

    @Test
    @SneakyThrows
    void dislikeFilmTest() {
        service.addFilm(createTestFilm());
        userService.addUser(createTestUser());
        assertEquals(0, service.getFilm(1).getLikes().size());
        service.likeFilm(1,1);
        assertEquals(1, service.getFilm(1).getLikes().size());
        service.dislikeFilm(1,1);
        assertEquals(0, service.getFilm(1).getLikes().size());
    }
}
