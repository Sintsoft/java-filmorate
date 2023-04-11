package ru.yandex.practicum.filmorate.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.utility.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.utility.exceptions.NullPayloadObjectException;
import ru.yandex.practicum.filmorate.utility.exceptions.UserNotFoundException;

import javax.validation.ValidationException;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class FilmService {

    @Autowired
    private final FilmStorage storage;

    @Autowired
    private final UserStorage userStorage;

    private static final FilmPopularityComparator popularityComparator = new FilmPopularityComparator();

    public Film addFilm(Film film) {
        if (film == null) {
            log.debug("Got null as film in addFilm function");
            throw new NullPayloadObjectException("Nothing in payload");
        }
        filmCheck(film);
        storage.addFilm(film);

        return film;
    }

    public Film getFilm(int id) {
        Film film = storage.getFilm(id);
        if (film == null) {
            throw new FilmNotFoundException("Film with id = " + id + " not found");
        }
        return film;
    }

    public Film likeFilm(int filmId, int userId) {
        log.trace("User " + userId + " liked film " + filmId);
        Film film = storage.getFilm(filmId);
        if (userStorage.getUser(userId) == null) {
            throw new UserNotFoundException("User not fond");
        }
        if (film == null) {
            throw new FilmNotFoundException("No such film");
        }
        film.likeFilm(userId);
        return film;
    }

    public List<Film> getAllFilms() {
        log.trace("Getting all films");
        return storage.getAllFilms();
    }

    public Film updateFilm(Film film) {
        if (film == null) {
            log.debug("Got null as film in updateFilm function");
            throw new NullPayloadObjectException("Nothing in payload");
        }
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

    public void dislikeFilm(int filmId, int userId) {
        log.trace("User " + userId + " disliked film " + filmId);
        Film film = storage.getFilm(filmId);
        if (userStorage.getUser(userId) == null) {
            throw new UserNotFoundException("User not fond");
        }
        if (film == null) {
            throw new FilmNotFoundException("No such film");
        }
        film.dislikeFilm(userId);
    }

    public List<Film> getMostLikedFilms(int amount) {
        Set<Film> sortedByLikes = new TreeSet<>(popularityComparator);
        sortedByLikes.addAll(storage.getAllFilms());
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
