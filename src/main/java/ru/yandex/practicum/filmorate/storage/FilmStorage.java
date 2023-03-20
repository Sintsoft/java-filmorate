package ru.yandex.practicum.filmorate.storage;

import java.util.List;

import ru.yandex.practicum.filmorate.model.Film;

public interface FilmStorage {
    

    void addFilm(Film film);

    void deleteFilm(Film film);

    void updateFilm(Film film);

    Film getFilm(int id);

    List<Film> getAllFilms();

}
