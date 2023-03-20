package ru.yandex.practicum.filmorate.services;

import java.util.List;

import javax.validation.ValidationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

@Service
@Slf4j
public class FilmService {
    
    @Autowired
    private FilmStorage storage;
    private int filmIdIterator = 1;

    public Film addFilm(Film film) {
        film.setId(filmIdIterator);
        filmCheck(film);
        storage.addFilm(film);
        filmIdIterator++; // Итерируем после того как успешно добавили
        log.trace("Film itreator afer add new film = " + filmIdIterator);
        return film;
    }

    public List<Film> getAllFilms() {
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
}
