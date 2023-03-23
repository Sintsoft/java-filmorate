package ru.yandex.practicum.filmorate.services;

import java.util.List;

import javax.validation.ValidationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.utility.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.utility.exceptions.UserNotFoundException;

@Service
@Slf4j
public class FilmService {
    
    @Autowired
    private FilmStorage storage;
    
    @Autowired
    private UserStorage userStorage;

    private int filmIdIterator = 1;

    public Film addFilm(Film film) {
        film.setId(filmIdIterator);
        filmCheck(film);
        storage.addFilm(film);
        filmIdIterator++; // Итерируем после того как успешно добавили
        log.trace("Film itreator afer add new film = " + filmIdIterator);
        return film;
    }

    public Film getFilm(int id) {
        Film film = storage.getFilm(id);
        if (film == null) {
            throw new FilmNotFoundException("Film with id = " + id + " not found");
        }
        return film;
    }

    public Film likeFilm(int filmId, int userId) {
        log.trace("User " + userId + " liked film " + filmId);
        Film film = storage.getFilm(filmId);
        if (userStorage.getUser(userId) == null ) {
            throw new UserNotFoundException("User not fond");
        }
        if (film == null) {
            throw new FilmNotFoundException("No such film");
        }
        film.likeFilm(userId);
        return film;
    }

    public List<Film> getAllFilms() {
        log.trace("Getting all films");
        return storage.getAllFilms();
    }

    public Film updateFilm(Film film) {
        log.trace("Updating user with id = " + film.getId());
        filmCheck(film);
        storage.updateFilm(film);
        return film;
    }

    private void filmCheck(Film film) {
        if (film.getDuration() <= 0) {
            log.info("Duration must be positive");
            throw new ValidationException("Duration must be positive");
        }
    }

    public void dislikeFilm(int filmId, int userId) {
        log.trace("User " + userId + " disliked film " + filmId);
        Film film = storage.getFilm(filmId);
        if (userStorage.getUser(userId) == null ) {
            throw new UserNotFoundException("User not fond");
        }
        if (film == null) {
            throw new FilmNotFoundException("No such film");
        }
        film.likeFilm(userId);
    }
}
