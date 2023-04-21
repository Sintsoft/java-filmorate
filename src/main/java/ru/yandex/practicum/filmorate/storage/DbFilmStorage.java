package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.utility.DataBaseConnectionParams;
import ru.yandex.practicum.filmorate.utility.exceptions.DatabaseConnectionEхception;
import ru.yandex.practicum.filmorate.utility.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.utility.exceptions.IncorrectEntityIDException;
import ru.yandex.practicum.filmorate.utility.exceptions.UserNotFoundException;

import java.sql.*;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@Slf4j
@Primary
public class DbFilmStorage implements FilmStorage {

    @Autowired
    UserStorage userStorage;

    @Autowired
    DataBaseConnectionParams params;

    @Override
    public void addFilm(Film film) {
        log.trace("Level: Storage. Method: addFilm. Input: " + film);
        if (film.getId() != 0) {
            log.trace("Added film have incorrect id - " + film.getId());
            throw new IncorrectEntityIDException("Wrong method! User with id should be equal to 0");
        }
        try (
                Connection connection = DriverManager.getConnection(
                        params.getUrl(),
                        params.getUserName(),
                        params.getPassword()
                );
                PreparedStatement preparedStatement = connection.prepareStatement(
                        "INSERT INTO FILMS (NAME, DESCRIPTION, RELEASE_DATE, DURATION) VALUES (?, ?, ?, ?)",
                        Statement.RETURN_GENERATED_KEYS);
        ) {
            preparedStatement.setString(1, film.getName());
            preparedStatement.setString(2, film.getDescription());
            preparedStatement.setDate(3, Date.valueOf(film.getReleaseDate()));
            preparedStatement.setLong(4, film.getDuration());
            preparedStatement.executeUpdate();
            ResultSet result = preparedStatement.getGeneratedKeys();
            while (result.next()) {
                log.debug("Got film id = " + result.getInt(1));
                film.setId(result.getInt(1));
            }
        } catch (SQLException e) {
            log.error("Failed adding film due to: " + e.getClass());
            throw new DatabaseConnectionEхception("Failed to add film");
        }

    }

    @Override
    public void deleteFilm(Film film) {
        log.debug("Deleting FILM - " + film);
        try (
                Connection connection = DriverManager.getConnection(
                        params.getUrl(),
                        params.getUserName(),
                        params.getPassword()
                );
                PreparedStatement preparedStatement = connection.prepareStatement(
                        "DELETE FROM FILMS WHERE ID = ?",
                        Statement.RETURN_GENERATED_KEYS);
        ) {
            log.debug("Succesfully establihed connection to databae - " + params.getUrl());
            preparedStatement.setInt(1, film.getId());
            int rows = preparedStatement.executeUpdate();
            if (rows == 0 ) {
                log.trace("Wrong film id. Throwing eception");
                throw new FilmNotFoundException("No film with id - " + film.getId());
            }
            log.trace("Succesfully removed film!");
        } catch (SQLException e) {
            log.error("Failed delete film due to: " + e.getClass());
            throw new DatabaseConnectionEхception("Failed to delete film");
        }
    }

    @Override
    public void updateFilm(Film film) {
        log.debug("Deleting FILM - " + film);
        try (
                Connection connection = DriverManager.getConnection(
                        params.getUrl(),
                        params.getUserName(),
                        params.getPassword()
                );
                PreparedStatement preparedStatement = connection.prepareStatement(
                        "UPDATE FILMS SET NAME = ?, DESCRIPTION = ?, RELEASE_DATE = ?, DURATION = ? WHERE ID = ?",
                        Statement.RETURN_GENERATED_KEYS);
        ) {
            log.debug("Succesfully establihed connection to databae - " + params.getUrl());
            preparedStatement.setString(1, film.getName());
            preparedStatement.setString(2, film.getDescription());
            preparedStatement.setDate(3, Date.valueOf(film.getReleaseDate()));
            preparedStatement.setLong(4, film.getDuration());
            preparedStatement.setInt(5, film.getId());
            int rows = preparedStatement.executeUpdate();
            if (rows == 0 ) {
                log.trace("Wrong film id. Throwing eception");
                throw new FilmNotFoundException("No film with id - " + film.getId());
            }
            log.trace("Succesfully updated film!");
        } catch (SQLException e) {
            log.error("Failed delete film due to: " + e.getClass());
            throw new DatabaseConnectionEхception("Failed to delete film");
        }
    }

    @Override
    public Film getFilmById(int id) {
        log.trace("Level: Storage. Method: getFilmById. Input: " + id);
        try (
                Connection connection = DriverManager.getConnection(
                        params.getUrl(),
                        params.getUserName(),
                        params.getPassword()
                );
                PreparedStatement preparedStatement = connection.prepareStatement(
                        "SELECT * FROM FILMS WHERE id = ?",
                        Statement.RETURN_GENERATED_KEYS);
        ) {
            log.debug("Succesfully establihed connection to databae - " + params.getUrl());
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            Film returnFilm = null;
            while (resultSet.next()) {
                returnFilm = new Film(
                        resultSet.getInt(1),
                        resultSet.getString(2),
                        resultSet.getString(3),
                        resultSet.getDate(4).toLocalDate(),
                        Duration.ofMinutes(resultSet.getLong(5))
                );
                for (Integer userId : getFilmLikes(returnFilm.getId())) {
                    returnFilm.likeFilm(userId);
                }
            }
            return returnFilm;
        } catch (SQLException e) {
            log.error("Failed getting film due to: " + e.getClass());
            throw new DatabaseConnectionEхception("Failed to get film");
        }
    }

    @Override
    public List<Film> getAllFilms() {
        log.trace("Level: Storage. Method: getFilmById.");
        List<Film> allFilms = new ArrayList<>();
        try (
                Connection connection = DriverManager.getConnection(
                        params.getUrl(),
                        params.getUserName(),
                        params.getPassword()
                );
                PreparedStatement preparedStatement = connection.prepareStatement(
                        "SELECT * FROM FILMS",
                        Statement.RETURN_GENERATED_KEYS);
        ) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Film filmToAdd = new Film(
                        resultSet.getInt(1),
                        resultSet.getString(2),
                        resultSet.getString(3),
                        resultSet.getDate(4).toLocalDate(),
                        Duration.ofMinutes(resultSet.getLong(5))
                );
                for (Integer userId : getFilmLikes(filmToAdd.getId())) {
                    filmToAdd.likeFilm(userId);
                }
                allFilms.add(filmToAdd);
            }
            return allFilms;
        } catch (SQLException e) {
            log.error("Failed get films friends due to: " + e.getClass());
            throw new DatabaseConnectionEхception("Failed to get all films film");
        }
    }

    @Override
    public void saveLike(int userId, int filmId) {
        try (
                Connection connection = DriverManager.getConnection(
                        params.getUrl(),
                        params.getUserName(),
                        params.getPassword()
                );
                PreparedStatement preparedStatement = connection.prepareStatement(
                        "INSERT INTO LIKES (USER_ID, FILM_ID) VALUES (?, ?)",
                        Statement.RETURN_GENERATED_KEYS);
        ) {
            log.debug("Succesfully establihed connection to databae - " + params.getUrl());
            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, filmId);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            log.error("Failed get all users due to: " + e.getClass());
            throw new DatabaseConnectionEхception("Failed to save liked film " + filmId + " by user " + userId);
        }
    }

    @Override
    public void removeLike(int userId, int filmId) {
        log.trace("Level: Storage. Method: getFilmById.");
        try (
                Connection connection = DriverManager.getConnection(
                        params.getUrl(),
                        params.getUserName(),
                        params.getPassword()
                );
                PreparedStatement preparedStatement = connection.prepareStatement(
                        "DELETE FROM LIKES WHERE USER_ID = ? AND FILM_ID = ?",
                        Statement.RETURN_GENERATED_KEYS);
        ) {
            log.debug("Succesfully establihed connection to databae - " + params.getUrl());
            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, filmId);
            int rows = preparedStatement.executeUpdate();
            if (rows == 0 ) {
                log.trace("Wrong user id. Throwing eception");
                throw new FilmNotFoundException("Unable to erase friendship beetween users");
            }
        } catch (SQLException e) {
            log.error("Failed get films friends due to: " + e.getClass());
            throw new DatabaseConnectionEхception("Failed to save liked film " + filmId + " by user " + userId);
        }
    }

    @Override
    public List<Film> getMostLikedFilms(int amount) {
        log.trace("Level: Storage. Method: getFilmById.");
        try (
                Connection connection = DriverManager.getConnection(
                        params.getUrl(),
                        params.getUserName(),
                        params.getPassword()
                );
                PreparedStatement preparedStatement = connection.prepareStatement(
                        "SELECT F.*\n" +
                                "FROM (\n" +
                                "\tSELECT FILM_ID, COUNT(USER_ID) AS AMOUNT\n" +
                                "\tFROM LIKES\n" +
                                "\tGROUP BY FILM_ID \n" +
                                "\tORDER BY AMOUNT DESC \n" +
                                "\tLIMIT ?\n" +
                                ") AS C\n" +
                                "LEFT JOIN FILMS F\n" +
                                "\tON C.FILM_ID = F.ID",
                        Statement.RETURN_GENERATED_KEYS);
        ) {
            log.debug("Succesfully establihed connection to databae - " + params.getUrl());
            List<Film> mostLikedFilms = new ArrayList<>();
            preparedStatement.setInt(1, amount);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Film filmToAdd = new Film(
                        resultSet.getInt(1),
                        resultSet.getString(2),
                        resultSet.getString(3),
                        resultSet.getDate(4).toLocalDate(),
                        Duration.ofMinutes(resultSet.getLong(5))
                );
                for (Integer userId : getFilmLikes(filmToAdd.getId())) {
                    filmToAdd.likeFilm(userId);
                }
                mostLikedFilms.add(filmToAdd);
            }
            return mostLikedFilms;
        } catch (SQLException e) {
            log.error("Failed get films friends due to: " + e.getClass());
            throw new DatabaseConnectionEхception("Failed to get most popular films");
        }
    }

    public Set<Integer> getFilmLikes(int filmId) {
        log.trace("Level: Storage. Method: getFilmById.");
        try (
                Connection connection = DriverManager.getConnection(
                        params.getUrl(),
                        params.getUserName(),
                        params.getPassword()
                );
                PreparedStatement preparedStatement = connection.prepareStatement(
                        "SELECT * FROM LIKES WHERE FILM_ID = ?",
                        Statement.RETURN_GENERATED_KEYS);
        ) {
            log.debug("Succesfully establihed connection to databae - " + params.getUrl());
            preparedStatement.setInt(1, filmId);
            ResultSet resultSet = preparedStatement.executeQuery();
            Set<Integer> likesSet = new HashSet<>();
            while (resultSet.next()) {
                likesSet.add(
                        resultSet.getInt(1)
                );
            }
            return likesSet;
        } catch (SQLException e) {
            log.error("Failed get films friends due to: " + e.getClass());
            throw new DatabaseConnectionEхception("Failed to get all films film");
        }
    }
}
