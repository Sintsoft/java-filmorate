package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/genres")
@RequiredArgsConstructor
public class GanreController {

    @Autowired
    private final GenreService service;

    @GetMapping("/{genreId}")
    public Genre getGenre(@PathVariable int genreId) {
        return service.getGenre(genreId);
    }

    @GetMapping
    public List<Genre> getAllGenres() {
        return service.getAllGenres();
    }
}
