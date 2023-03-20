package ru.yandex.practicum.filmorate.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.services.FilmService;
import ru.yandex.practicum.filmorate.utility.exceptions.ValidationException;

@RestController
@Slf4j
@RequestMapping("/films")
public class FilmController {
    
    @Autowired
    private FilmService filmService;

    @GetMapping
    public List<Film> getAllFilms() {
        log.trace("Call /films GET request");
        return filmService.getAllFilms();
    }

    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) {
        log.trace("Call /films PUT request");
        if (film == null) {
            log.info("Null film in post request");
            throw new ValidationException("Null film data");
        }
        filmService.addFilm(film);
        return film;
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        log.trace("Call /films PUT request");
        if (film == null) {
            log.info("Null film in post request");
            throw new ValidationException("Null film data");
        }
        filmService.updateFilm(film);
        return film;
    }

}
