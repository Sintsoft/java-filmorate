package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.utility.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.utility.exceptions.IncorrectEntityIDException;
import ru.yandex.practicum.filmorate.utility.exceptions.UserNotFoundException;

import javax.validation.ValidationException;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Integer, Film> inMemoryStorage = new HashMap<>();
    int filmIdIterator = 1;

    @Autowired
    InMemoryUserStorage userStorage;

    private static final FilmPopularityComparator popularityComparator = new FilmPopularityComparator();


    @Override
    public void addFilm(Film film) {
        log.trace("Level: Storage. Method: addFilm. Input: " + film);
        if (film.getId() != 0) {
            log.trace("Added film have incorrect id - " + film.getId());
            throw new IncorrectEntityIDException("Wrong method! Film with id should be updated");
        } else {
            film.setId(filmIdIterator);
            log.trace("Set added film id - " + filmIdIterator);
            inMemoryStorage.put(film.getId(), film);
            filmIdIterator++;
            log.trace("Next film id will be = " + filmIdIterator);
        }
    }

    @Override
    public void deleteFilm(Film film) {
        if (!inMemoryStorage.containsKey(film.getId())) throw new FilmNotFoundException("Film not found");
        inMemoryStorage.remove(film.getId());
    }

    @Override
    public void updateFilm(Film film) {
        if (!inMemoryStorage.containsKey(film.getId())) throw new FilmNotFoundException("Film not found");
        inMemoryStorage.put(film.getId(), film);
    }

    @Override
    public Film getFilmById(int id) {
        log.trace("Level: Storage. Method: getFilmById. Input: " + id);
        return inMemoryStorage.get(id);
    }

    @Override
    public List<Film> getAllFilms() {
        log.trace("Level: Storage. Method: getAllFilms.");
        return List.copyOf(inMemoryStorage.values());
    }

    @Override
    public void saveLike(int userId, int filmId) {
        log.trace("Level: Storage. Method: saveLike. Input: " + userId + ", " + filmId);
        if (!inMemoryStorage.containsKey(filmId)) {
            log.info("Wrong film id");
            throw new FilmNotFoundException("Wrong film id");
        } else if (userStorage.getUser(userId) == null) {
            log.info("Wrong user id");
            throw new UserNotFoundException("Wrong film id");
        }
        inMemoryStorage.get(filmId).likeFilm(userId);
    }

    @Override
    public void removeLike(int userId, int filmId) {
        log.trace("Level: Storage. Method: saveLike. Input: " + userId + ", " + filmId);
        if (!inMemoryStorage.containsKey(filmId)) {
            log.info("Wrong film id");
            throw new FilmNotFoundException("Wrong film id");
        } else if (userStorage.getUser(userId) == null) {
            log.info("Wrong user id");
            throw new UserNotFoundException("Wrong film id");
        }
        inMemoryStorage.get(filmId).dislikeFilm(userId);
    }

    @Override
    public List<Film> getMostLikedFilms(int amount) {
        Set<Film> sortedByLikes = new TreeSet<>(popularityComparator);
        sortedByLikes.addAll(inMemoryStorage.values());
        return sortedByLikes.stream().limit(amount).collect(Collectors.toList());
    }

    static class FilmPopularityComparator implements Comparator<Film> {

        @Override
        public int compare(Film o1, Film o2) {
            int comparsion = o2.getLikes().size() - o1.getLikes().size();
            if (comparsion == 0) {
                comparsion = o2.getName().compareTo(o1.getName());
            }
            return comparsion;
        }
    }
}
