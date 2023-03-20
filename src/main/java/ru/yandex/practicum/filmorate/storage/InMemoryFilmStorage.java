package ru.yandex.practicum.filmorate.storage;

import java.util.HashMap;
import java.util.Map;

import javax.validation.ValidationException;

import java.util.List;

import org.springframework.stereotype.Component;

import ru.yandex.practicum.filmorate.model.Film;

@Component
public class InMemoryFilmStorage implements FilmStorage{

    Map<Integer, Film> films = new HashMap<>();

    @Override
    public void addFilm(Film film) {
        if (!films.containsKey(film.getId()) && film.getId() > 0) {
            films.put(
                film.getId(), film
            );
        } else {
            throw new ValidationException("Add film failed - invaild user");
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
            throw new ValidationException("Update canceled. Can't find film with id: " + film.getId());
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
