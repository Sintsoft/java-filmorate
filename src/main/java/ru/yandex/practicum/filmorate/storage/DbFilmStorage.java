package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.utility.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.utility.exceptions.IncorrectEntityIDException;
import ru.yandex.practicum.filmorate.utility.exceptions.UserNotFoundException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

@Component
@Slf4j
@Primary
public class DbFilmStorage implements FilmStorage {

    private static final String INSERT_FILM_QUERY = "INSERT INTO FILMS (NAME, DESCRIPTION, RELEASE_DATE, DURATION, MPA_ID) " +
            "VALUES (?, ?, ?, ?, ?)";
    private static final String DELETE_FILM_QUERY = "DELETE FROM FILMS WHERE ID = ?";
    private static final String UPDATE_FILM_QUERY = "UPDATE FILMS SET NAME = ?, DESCRIPTION = ?, RELEASE_DATE = ?, " +
            "DURATION = ?, MPA_ID = ? WHERE ID = ?";
    private static final String GET_FILM_QUERY = "SELECT * FROM FILMS WHERE id = ?";
    private static final String GET_ALL_FILMS_QUERY = "SELECT * FROM FILMS";
    private static final String INSERT_LIKE_QUERY = "INSERT INTO LIKES (USER_ID, FILM_ID) VALUES (?, ?)";
    private static final String DELETE_LIKE_QUERY = "DELETE FROM LIKES WHERE USER_ID = ? AND FILM_ID = ?";
    private static final String GET_MOST_LIKED_FILMS_QUERY = "SELECT f.*, COUNT(l.USER_ID) AS AMOUNT\n" +
            "FROM FILMS f \n" +
            "LEFT JOIN LIKES l\n" +
            "\tON f.ID = l.FILM_ID \n" +
            "GROUP BY f.ID, f.NAME, f.DESCRIPTION, f.RELEASE_DATE, f.DURATION, f.MPA_ID \n" +
            "ORDER BY AMOUNT DESC, f.NAME ASC\n" +
            "LIMIT ?";
    private static final String GET_FILM_LIKES = "SELECT USER_ID FROM LIKES WHERE FILM_ID = ?";

    @Autowired
    MPAStorage mpaStorage;

    @Autowired
    DBGenreStorage genreStorage;

    @Autowired
    JdbcTemplate jdbc;

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
            for (Integer userId : getFilmLikes(film.getId())) {
                film.likeFilm(userId);
            }
        } catch (EmptyResultDataAccessException e) {
            log.info("Film with id = " + id + " not found.");
        }
        return film;
    }

    @Override
    public List<Film> getAllFilms() {
        log.trace("Level: Storage. Method: getFilmById.");
        return parseRowSet(
                jdbc.queryForRowSet(
                        GET_ALL_FILMS_QUERY
                ));
    }

    @Override
    public void saveLike(int userId, int filmId) {
        log.trace("Level: Storage. Method: saveLike.");
        jdbc.update(INSERT_LIKE_QUERY, new Object[]{userId, filmId});
    }

    @Override
    public void removeLike(int userId, int filmId) {
        log.trace("Level: Storage. Method: getFilmById.");
        jdbc.update(DELETE_LIKE_QUERY, new Object[]{userId, filmId});
    }

    @Override
    public List<Film> getMostLikedFilms(int amount) {
        log.trace("Level: Storage. Method: getMostLikedFilms.");
        return parseRowSet(
                jdbc.queryForRowSet(
                        GET_MOST_LIKED_FILMS_QUERY,
                        new Object[]{amount}
                ));
    }

    public Set<Integer> getFilmLikes(int filmId) {
        log.trace("Level: Storage. Method: getFilmById.");
        Set<Integer> likesSet = new TreeSet<>();
        SqlRowSet resultSet = jdbc.queryForRowSet(
                GET_FILM_LIKES,
                new Object[]{filmId}
        );
        while (resultSet.next()) {
            likesSet.add(
                    resultSet.getInt(1)
            );
        }
        return likesSet;
    }

    private List<Film> parseRowSet(SqlRowSet resultSet) {
        List<Film> allFilms = new ArrayList<>();
        while (resultSet.next()) {
            Film filmToAdd = new Film(
                    resultSet.getInt(1),
                    resultSet.getString(2),
                    resultSet.getString(3),
                    resultSet.getDate(4).toLocalDate(),
                    resultSet.getLong(5),
                    mpaStorage.getMPA(resultSet.getInt(6)).get()
            );
            filmToAdd.setGenres(genreStorage.getFilmGenres(resultSet.getInt(1)));
            for (Integer userId : getFilmLikes(filmToAdd.getId())) {
                filmToAdd.likeFilm(userId);
            }
            allFilms.add(filmToAdd);
        }
        return allFilms;
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
