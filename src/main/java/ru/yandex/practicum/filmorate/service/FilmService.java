package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.utility.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.utility.exceptions.NullPayloadObjectException;

import javax.validation.ValidationException;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class FilmService {

    @Autowired
    private final FilmStorage storage;

    @Autowired
    private final UserStorage userStorage;


    public Film addFilm(Film film) {
        if (film == null) {
            log.debug("Got null as film in addFilm function");
            throw new NullPayloadObjectException("Nothing in payload");
        }
        filmCheck(film);
        storage.addFilm(film);

        return film;
    }

    public Film getFilm(int id) {
        Film film = storage.getFilmById(id);
        if (film == null) {
            throw new FilmNotFoundException("Film with id = " + id + " not found");
        }
        return film;
    }

    public Film likeFilm(int filmId, int userId) {
        log.trace("User " + userId + " liked film " + filmId);
        storage.saveLike(userId, filmId);
        return storage.getFilmById(filmId);
    }

    public List<Film> getAllFilms() {
        log.trace("Getting all films");
        return storage.getAllFilms();
    }

    public Film updateFilm(Film film) {
        log.trace("Updating user with id = " + film.getId());
        if (film == null) {
            log.debug("Got null as film in updateFilm function");
            throw new NullPayloadObjectException("Nothing in payload");
        }
        filmCheck(film);
        storage.updateFilm(film);
        return film;
    }

    public void dislikeFilm(int filmId, int userId) {
        log.trace("User " + userId + " disliked film " + filmId);
        storage.removeLike(userId, filmId);
    }

    public List<Film> getMostLikedFilms(int amount) {
        return storage.getMostLikedFilms(amount);
    }

    private void filmCheck(Film film) {
        if (film.getDuration() <= 0) {
            log.info("Duration must be positive");
            throw new ValidationException("Duration must be positive");
        }
    }

}
