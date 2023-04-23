package ru.yandex.practicum.filmorate.storage;

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
public class DBGenreStorage implements GenreStorage {

    private final String GET_GENRE_QUERY = "SELECT * FROM GENRE WHERE ID = ?";
    private final String GET_ALL_GENRES_QUERY = "SELECT * FROM GENRE";
    private final String GET_FILM_GENRES_QUERY = "SELECT DISTINCT g.*\n" +
            "FROM FILMGENRE f \n" +
            "LEFT JOIN GENRE g\n" +
            "\tON g.ID = f.GENRE_ID \n" +
            "WHERE f.FILM_ID = ?";
    private final String INSERT_FILM_GENRE_QUERY = "INSERT INTO FILMGENRE (FILM_ID, GENRE_ID) VALUES (?, ?)";
    private final String DELETE_FILM_GENRES_QUERY = "DELETE FROM FILMGENRE WHERE FILM_ID = ?";

    @Autowired
    JdbcTemplate jdbc;

    public Optional<Genre> getGenre(int id) {
        log.trace("Level: Storage. Method: getGenre. Input: " + id);
        Optional<Genre> genre = Optional.empty();
        try {
            Object obj = jdbc.queryForObject(
                    GET_GENRE_QUERY,
                    new Object[]{id},
                    new GenreRowMapper()
            );
            if (obj != null) {
                genre = Optional.of(Genre.class.cast(obj));
            }
        } catch (EmptyResultDataAccessException e) {
            log.info("Genre with id = " + id + " not exist");;
        }
        return genre;
    }

    public List<Genre> getAllGenres() {
        log.trace("Level: Storage. Method: getAllGenres.");
        List<Genre> returnGenre = new ArrayList<>();
        SqlRowSet resultSet = jdbc.queryForRowSet(GET_ALL_GENRES_QUERY);
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
                    new Object[]{
                            film.getId(),
                            genre.getId()
                    }
            );
        }
        film.setGenres(getFilmGenres(film.getId()));
    }

    private void dropFilmGenres(int filmId) {
        log.trace("Level: Storage. Method: dropFilmGenres.");
        jdbc.update(DELETE_FILM_GENRES_QUERY, filmId);
    }

    private static class GenreRowMapper implements RowMapper {

        @Override
        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Genre(
                    rs.getInt(1),
                    rs.getString(2)
            );
        }
    }
}
