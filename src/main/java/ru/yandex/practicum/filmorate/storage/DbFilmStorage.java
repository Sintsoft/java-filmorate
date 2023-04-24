package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.utility.exceptions.EntityNotFoundException;
import ru.yandex.practicum.filmorate.utility.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.utility.exceptions.IncorrectEntityIDException;
import ru.yandex.practicum.filmorate.utility.exceptions.UserNotFoundException;

import java.sql.*;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

@Component
@Slf4j
@Primary
@RequiredArgsConstructor
public class DbFilmStorage implements FilmStorage {

    private static final String INSERT_FILM_QUERY = "INSERT INTO FILMS " +
            "(NAME, DESCRIPTION, RELEASE_DATE, DURATION, MPA_ID) " +
            "VALUES (?, ?, ?, ?, ?)";
    private static final String DELETE_FILM_QUERY = "DELETE FROM FILMS WHERE ID = ?";
    private static final String UPDATE_FILM_QUERY = "UPDATE FILMS SET NAME = ?, DESCRIPTION = ?, RELEASE_DATE = ?, " +
            "DURATION = ?, MPA_ID = ? WHERE ID = ?";
    private static final String GET_FILM_QUERY = "SELECT * FROM FILMS WHERE id = ?";
    private static final String GET_ALL_FILMS_QUERY = "SELECT * FROM FILMS";
    private static final String INSERT_LIKE_QUERY = "INSERT INTO LIKES (USER_ID, FILM_ID) VALUES (?, ?)";
    private static final String DELETE_LIKE_QUERY = "DELETE FROM LIKES WHERE USER_ID = ? AND FILM_ID = ?";
    private static final String GET_MOST_LIKED_FILMS_QUERY = "SELECT " +
            "ID, NAME, DESCRIPTION, RELEASE_DATE, DURATION, MPA_ID " +
            "FROM (SELECT f.*, COUNT(l.USER_ID) AS AMOUNT " +
            "FROM FILMS f " +
            "LEFT JOIN LIKES l " +
            "ON f.ID = l.FILM_ID " +
            "GROUP BY f.ID, f.NAME, f.DESCRIPTION, f.RELEASE_DATE, f.DURATION, f.MPA_ID " +
            "ORDER BY AMOUNT DESC, f.NAME ASC) " +
            "LIMIT ?";
    private static final String GET_FILM_LIKES = "SELECT USER_ID FROM LIKES WHERE FILM_ID = ?";

    @Autowired
    private final MPAStorage mpaStorage;

    @Autowired
    private final DBGenreStorage genreStorage;

    @Autowired
    private final JdbcTemplate jdbc;

    @Override
    public void addFilm(Film film) {
        log.trace("Level: Storage. Method: addFilm. Input: " + film);
        if (film.getId() != 0) {
            log.trace("Added film have incorrect id - " + film.getId());
            throw new IncorrectEntityIDException("Wrong method! User with id should be equal to 0");
        }
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(
                        INSERT_FILM_QUERY,
                    Statement.RETURN_GENERATED_KEYS
                );
                ps.setString(1, film.getName());
                ps.setString(2, film.getDescription());
                ps.setDate(3, Date.valueOf(film.getReleaseDate()));
                ps.setLong(4, film.getDuration());
                ps.setInt(5, film.getMpa().getId());
                return ps;
            }, keyHolder
        );
        film.setId(keyHolder.getKey().intValue());
        genreStorage.saveFilmGenres(film);
    }

    @Override
    public void deleteFilm(Film film) {
        log.debug("Deleting FILM - " + film);
        int result = jdbc.update(DELETE_FILM_QUERY, film.getId());
        if (result == 0) {
            log.info("Deletion failed. No user with id: " + film.getId());
            throw new UserNotFoundException("User with id "
                    + film.getId() + "was not found.");
        }
    }

    @Override
    public void updateFilm(Film film) {
        log.debug("Deleting FILM - " + film);
        int result = jdbc.update(UPDATE_FILM_QUERY,
            film.getName(),
            film.getDescription(),
            Date.valueOf(film.getReleaseDate()),
            film.getDuration(),
            film.getMpa().getId(),
            film.getId()
        );
        genreStorage.saveFilmGenres(film);
        if (result == 0) {
            log.trace("Wrong film id. Throwing eception");
            throw new FilmNotFoundException("No film with id - " + film.getId());
        }
    }

    @Override
    public Film getFilmById(int id) {
        log.trace("Level: Storage. Method: getFilmById. Input: " + id);
        Film film = null;
        try {
            film = jdbc.queryForObject(
                    GET_FILM_QUERY,
                    new Object[]{id},
                    new FilmRowMapper()
            );
            fillUpFilmInfo(film);
        } catch (EmptyResultDataAccessException e) {
            log.info("Film with id = " + id + " not found.");
        }
        return film;
    }

    @Override
    public List<Film> getAllFilms() {
        log.trace("Level: Storage. Method: getFilmById.");
        List<Film> allFilms = jdbc.query(GET_ALL_FILMS_QUERY, new FilmRowMapper());
        for (Film film : allFilms) {
            fillUpFilmInfo(film);
        }
        return allFilms;
    }

    @Override
    public void saveLike(int userId, int filmId) {
        log.trace("Level: Storage. Method: saveLike.");
        jdbc.update(INSERT_LIKE_QUERY, new Object[]{userId, filmId});
    }

    @Override
    public void removeLike(int userId, int filmId) {
        log.trace("Level: Storage. Method: getFilmById.");
        int result = jdbc.update(DELETE_LIKE_QUERY, new Object[]{userId, filmId});
        if (result == 0) {
            log.info("Deletion failed.");
            throw new EntityNotFoundException("Like deletion failed");
        }
    }

    @Override
    public List<Film> getMostLikedFilms(int amount) {
        log.trace("Level: Storage. Method: getMostLikedFilms.");
        List<Film> mostLikedFilms = jdbc.query(GET_MOST_LIKED_FILMS_QUERY, new Object[]{amount}, new FilmRowMapper());
        for (Film film : mostLikedFilms) {
            fillUpFilmInfo(film);
        }
        return mostLikedFilms;
    }

    public Set<Integer> getFilmLikes(int filmId) {
        log.trace("Level: Storage. Method: getFilmById.");
        return new TreeSet<>(
                jdbc.queryForList(
                        GET_FILM_LIKES,
                        new Object[]{filmId},
                        Integer.class)
        );
    }


    private void fillUpFilmInfo(Film film) {
        film.setGenres(genreStorage.getFilmGenres(film.getId()));
        for (Integer userId : getFilmLikes(film.getId())) {
            film.likeFilm(userId);
        }
    }

    private class FilmRowMapper implements RowMapper<Film> {
        @Override
        public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Film(
                    rs.getInt(1),
                    rs.getString(2),
                    rs.getString(3),
                    rs.getDate(4).toLocalDate(),
                    rs.getLong(5),
                    mpaStorage.getMPA(rs.getInt(6)).get()
            );
        }
    }
}
