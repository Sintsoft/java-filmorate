package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.utility.DataBaseConnectionParams;
import ru.yandex.practicum.filmorate.utility.exceptions.DatabaseConnectionEхception;

import java.sql.*;
import java.util.*;

@Component
@Slf4j
@Primary
public class GenreStorage {

    @Autowired
    DataBaseConnectionParams params;

    public Optional<Genre> getGenre(int id) {
        log.trace("Level: Storage. Method: getGenre. Input: " + id);
        try (
                Connection connection = DriverManager.getConnection(
                        params.getUrl(),
                        params.getUserName(),
                        params.getPassword()
                );
                PreparedStatement preparedStatement = connection.prepareStatement(
                        "SELECT * FROM GENRE WHERE ID = ?",
                        Statement.RETURN_GENERATED_KEYS);
        ) {
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            Optional<Genre> returnGenre = Optional.empty();
            while (resultSet.next()) {
                returnGenre = Optional.of(new Genre(resultSet.getInt(1), resultSet.getString(2)));
            }
            return returnGenre;
        } catch (SQLException e) {
            log.error("Failed getting film due to: " + e.getClass());
            throw new DatabaseConnectionEхception("Failed to get genre");
        }
    }

    public List<Genre> getAllGenres() {
        log.trace("Level: Storage. Method: getAllGenres.");
        try (
                Connection connection = DriverManager.getConnection(
                        params.getUrl(),
                        params.getUserName(),
                        params.getPassword()
                );
                PreparedStatement preparedStatement = connection.prepareStatement(
                        "SELECT * FROM GENRE",
                        Statement.RETURN_GENERATED_KEYS);
        ) {
            ResultSet resultSet = preparedStatement.executeQuery();
            List<Genre> returnGenre = new ArrayList<>();
            while (resultSet.next()) {
                returnGenre.add(
                        new Genre(
                                resultSet.getInt(1),
                                resultSet.getString(2)
                        )
                );
            }
            return returnGenre;
        } catch (SQLException e) {
            log.error("Failed getting film due to: " + e.getClass());
            throw new DatabaseConnectionEхception("Failed to get all genres");
        }
    }

    public List<Genre> getFilmGenres(int filmID) {
        log.trace("Level: Storage. Method: getFilmGenres.");
        try (
                Connection connection = DriverManager.getConnection(
                        params.getUrl(),
                        params.getUserName(),
                        params.getPassword()
                );
                PreparedStatement preparedStatement = connection.prepareStatement(
                        "SELECT DISTINCT g.*\n" +
                                "FROM FILMGENRE f \n" +
                                "LEFT JOIN GENRE g\n" +
                                "\tON g.ID = f.GENRE_ID \n" +
                                "WHERE f.FILM_ID = ?",
                        Statement.RETURN_GENERATED_KEYS);
        ) {
            preparedStatement.setInt(1, filmID);
            ResultSet resultSet = preparedStatement.executeQuery();
            List<Genre> returnGenre = new ArrayList<>();
            while (resultSet.next()) {
                returnGenre.add(
                        new Genre(
                                resultSet.getInt(1),
                                resultSet.getString(2)
                        )
                );
            }
            return returnGenre;
        } catch (SQLException e) {
            log.error("Failed getting film due to: " + e.getClass());
            throw new DatabaseConnectionEхception("Failed to get film genres");
        }
    }

    public void saveFilmGenres(Film film) {
        log.trace("Level: Storage. Method: saveFilmGenres.");
        try (
                Connection connection = DriverManager.getConnection(
                        params.getUrl(),
                        params.getUserName(),
                        params.getPassword()
                );
                PreparedStatement preparedStatement = connection.prepareStatement(
                        "INSERT INTO FILMGENRE (FILM_ID, GENRE_ID) VALUES (?, ?)",
                        Statement.RETURN_GENERATED_KEYS);
        ) {
            this.dropFilmGenres(film.getId());
            if (film.getGenres() == null || film.getGenres().isEmpty()) {
                return;
            }
            Set<Integer> genreSet = new TreeSet<>();
            for (Genre genre : film.getGenres()) {
                genreSet.add(genre.getId());
            }
            preparedStatement.setInt(1, film.getId());
            for (Integer genreId : genreSet) {
                log.trace("Adding genre id = " + genreId + " to film id = " + film.getId());
                preparedStatement.setInt(2, genreId);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            log.error("Failed getting film due to: " + e.getClass());
            throw new DatabaseConnectionEхception("Failed to get film");
        }
    }

    private void dropFilmGenres(int filmId) {
        log.trace("Level: Storage. Method: dropFilmGenres.");
        try (
                Connection connection = DriverManager.getConnection(
                        params.getUrl(),
                        params.getUserName(),
                        params.getPassword()
                );
                PreparedStatement preparedStatement = connection.prepareStatement(
                        "DELETE FROM FILMGENRE WHERE FILM_ID = ?",
                        Statement.RETURN_GENERATED_KEYS);
        ) {
            preparedStatement.setInt(1, filmId);
            preparedStatement.executeUpdate();
            log.trace("Succesfull film genres drop");
        } catch (SQLException e) {
            log.error("Failed getting film due to: " + e.getClass());
            throw new DatabaseConnectionEхception("Failed to get film");
        }
    }
}
