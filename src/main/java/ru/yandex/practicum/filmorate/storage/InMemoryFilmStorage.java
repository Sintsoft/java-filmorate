package ru.yandex.practicum.filmorate.storage;

import java.util.HashMap;
import java.util.Map;

import javax.validation.ValidationException;

import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.utility.exceptions.FilmNotFoundException;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Integer, Film> films = new HashMap<>();

    private int filmIdIterator = 1;

    @Override
    public void addFilm(Film film) {
        if (!films.containsKey(film.getId()) && film.getId() == 0) {
            filmIdIterator++; // Итерируем после того как успешно добавили
            log.trace("Film iterator after add new film = " + filmIdIterator);
            films.put(
                film.getId(), film
            );
            filmIdIterator++;
        } else {
            throw new ValidationException("Add film failed - invaild film");
        }
    }

    @Override
    public void deleteFilm(Film film) {
        if (films.containsKey(film.getId())) {
            films.remove(film.getId());
        } else {
            throw new ValidationException("Wrong film to delete");
        }
    }

    @Override
    public void updateFilm(Film film) {
        if (films.containsKey(film.getId())) {
            films.put(
                film.getId(), film
            );
        } else {
            throw new FilmNotFoundException("Update canceled. Can't find film with id: " + film.getId());
        }
    }

    @Override
    public Film getFilm(int id) {
        return films.get(id);
    }

    @Override
    public List<Film> getAllFilms() {
        return List.copyOf(films.values());
    }

}