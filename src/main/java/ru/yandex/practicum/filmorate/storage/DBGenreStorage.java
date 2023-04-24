package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@Slf4j
@Primary
@RequiredArgsConstructor
public class DBGenreStorage implements GenreStorage {

    private static final String GET_GENRE_QUERY = "SELECT * FROM GENRE WHERE ID = ?";
    private static final String GET_ALL_GENRES_QUERY = "SELECT * FROM GENRE";
    private static final String GET_FILM_GENRES_QUERY = "SELECT DISTINCT g.* "
            + "FROM FILMGENRE f "
            + "LEFT JOIN GENRE g "
            + "ON g.ID = f.GENRE_ID "
            + "WHERE f.FILM_ID = ?";
    private static final String INSERT_FILM_GENRE_QUERY = "INSERT INTO FILMGENRE (FILM_ID, GENRE_ID) VALUES (?, ?)";
    private static final String DELETE_FILM_GENRES_QUERY = "DELETE FROM FILMGENRE WHERE FILM_ID = ?";

    @Autowired
    private final JdbcTemplate jdbc;

    public Optional<Genre> getGenre(int id) {
        log.trace("Level: Storage. Method: getGenre. Input: " + id);
        Optional<Genre> genre = Optional.empty();
        try {
            Genre obj = jdbc.queryForObject(
                    GET_GENRE_QUERY,
                    new Object[]{id},
                    new GenreRowMapper()
            );
            if (obj != null) {
                genre = Optional.of(obj);
            }
        } catch (EmptyResultDataAccessException e) {
            log.info("Genre with id = " + id + " not exist");;
        }
        return genre;
    }

    public List<Genre> getAllGenres() {
        log.trace("Level: Storage. Method: getAllGenres.");
        return jdbc.query(GET_ALL_GENRES_QUERY, new GenreRowMapper());
    }

    public List<Genre> getFilmGenres(int filmID) {
        log.trace("Level: Storage. Method: getFilmGenres.");
        List<Genre> returnGenre = new ArrayList<>();
        SqlRowSet resultSet = jdbc.queryForRowSet(GET_FILM_GENRES_QUERY, new Object[]{filmID});
        while (resultSet.next()) {
            returnGenre.add(
                new Genre(
                    resultSet.getInt(1),
                    resultSet.getString(2)
                )
            );
        }
        return returnGenre;
    }

    public void saveFilmGenres(Film film) {
        log.trace("Level: Storage. Method: saveFilmGenres.");
        dropFilmGenres(film.getId());
        for (Genre genre : film.getGenres()) {
            jdbc.update(
                    INSERT_FILM_GENRE_QUERY,
                    film.getId(),
                    genre.getId());
        }
        film.setGenres(getFilmGenres(film.getId()));
    }

    private void dropFilmGenres(int filmId) {
        log.trace("Level: Storage. Method: dropFilmGenres.");
        jdbc.update(DELETE_FILM_GENRES_QUERY, filmId);
    }

    private static class GenreRowMapper implements RowMapper<Genre> {

        @Override
        public Genre mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Genre(
                    rs.getInt(1),
                    rs.getString(2)
            );
        }
    }
}
