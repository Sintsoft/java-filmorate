package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {

    void addFilm(Film film);

    void deleteFilm(Film film);

    void updateFilm(Film film);

    Film getFilmById(int id);

    List<Film> getAllFilms();

    void saveLike(int userId, int filmId);

    void removeLike(int userId, int filmId);

    List<Film> getMostLikedFilms(int amount);
}
