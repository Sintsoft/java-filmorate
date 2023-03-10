package ru.yandex.practicum.filmorate.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.utility.exceptions.ValidationException;

@RestController
@Slf4j
@RequestMapping("/films")
public class FilmController {
    
    private final Map<Integer, Film> films = new HashMap<>();
    private int filmIdIterator = 1;

    @GetMapping
    public List<Film> getAllFilms() {
        log.trace("Call /films GET request");
        return List.copyOf(films.values());
    }

    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) {
        log.trace("Call /films PUT request");
        if (film == null) {
            log.info("Null film in post request");
            throw new ValidationException("Null film data");
        }
        if (films.containsKey(film.getId())) {
            log.info("Wrong film add method");
            throw new ValidationException("Wrong method");
        }
        if (film.getDuration() <= 0) {
            log.info("Duration must be positive");
            throw new ValidationException("Duration must be positive");
        }
        film.setId(filmIdIterator++);
        films.put(film.getId(), film);
        return film;
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        log.trace("Call /films PUT request");
        if (film == null) {
            log.info("Null film in post request");
            throw new ValidationException("Null film data");
        }
        if (!films.containsKey(film.getId())) {
            log.info("Not existing id PUT film");
            throw new ValidationException("Id is out of range");
        }
        if (film.getDuration() <= 0) {
            log.info("Duration must be positive");
            throw new ValidationException("Duration must be positive");
        }
        films.put(film.getId(), film);
        return film;
    }

}
