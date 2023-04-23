package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Optional;

public interface GenreStorage {

    Optional<Genre>  getGenre(int id);

    List<Genre> getAllGenres();

    List<Genre> getFilmGenres(int filmID);

    void saveFilmGenres(Film film);
}
