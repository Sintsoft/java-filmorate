package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.services.FilmService;
import ru.yandex.practicum.filmorate.services.UserService;

import java.time.Duration;
import java.time.LocalDate;

@SpringBootTest
public class FilmServiceTest {

    @Autowired
    private FilmService service;

    @Autowired
    private UserService userService;

    Film createTestFilm() {
        return new Film(0, Duration.ofMinutes(90), "Test film", "Test film description", LocalDate.of(2022,2,2));
    }

    User createTestUser() {
        return new User(0, "name", "login", "mail@mail.ru", LocalDate.of(2000, 1, 1));
    }

    @Test
    void addLikeToFilmTest() {
        service.addFilm(createTestFilm());
        userService.addUser(createTestUser());
        service.likeFilm(1,1);
        assertEquals(1, service.getFilm(1).getLikes().size());
    }

    @Test
    void dislikeFilmTest() {
        service.addFilm(createTestFilm());
        userService.addUser(createTestUser());
        service.likeFilm(1,1);
        assertEquals(1, service.getFilm(1).getLikes().size());
        service.dislikeFilm(1,1);
        assertEquals(0, service.getFilm(1).getLikes().size());
    }
}
