package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.utility.databasework.DataBaseConnectionParams;
import ru.yandex.practicum.filmorate.utility.exceptions.DatabaseConnectionException;
import ru.yandex.practicum.filmorate.utility.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.utility.exceptions.IncorrectEntityIDException;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@Slf4j
@Primary
public class DbFilmStorage implements FilmStorage {

    @Autowired
    DataBaseConnectionParams params;

    @Autowired
    MPAStorage mpaStorage;

    @Autowired
    GenreStorage genreStorage;

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
                        "INSERT INTO FILMS (NAME, DESCRIPTION, RELEASE_DATE, DURATION, MPA_ID) VALUES (?, ?, ?, ?, ?)",
                        Statement.RETURN_GENERATED_KEYS);
        ) {
            preparedStatement.setString(1, film.getName());
            preparedStatement.setString(2, film.getDescription());
            preparedStatement.setDate(3, Date.valueOf(film.getReleaseDate()));
            preparedStatement.setLong(4, film.getDuration());
            preparedStatement.setInt(5, film.getMpa().getId());
            preparedStatement.executeUpdate();
            log.trace("added film to DB");
            ResultSet result = preparedStatement.getGeneratedKeys();
            while (result.next()) {
                log.debug("Got film id = " + result.getInt(1));
                film.setId(result.getInt(1));
            }
            genreStorage.saveFilmGenres(film);
        } catch (SQLException e) {
            log.error("Failed adding film due to: " + e.getClass());
            throw new DatabaseConnectionException("Failed to add film");
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
            if (rows == 0) {
                log.trace("Wrong film id. Throwing eception");
                throw new FilmNotFoundException("No film with id - " + film.getId());
            }
            log.trace("Succesfully removed film!");
        } catch (SQLException e) {
            log.error("Failed delete film due to: " + e.getClass());
            throw new DatabaseConnectionException("Failed to delete film");
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
                        "UPDATE FILMS SET NAME = ?, DESCRIPTION = ?, RELEASE_DATE = ?, DURATION = ?, MPA_ID = ? WHERE ID = ?",
                        Statement.RETURN_GENERATED_KEYS);
        ) {
            log.debug("Succesfully establihed connection to databae - " + params.getUrl());
            preparedStatement.setString(1, film.getName());
            preparedStatement.setString(2, film.getDescription());
            preparedStatement.setDate(3, Date.valueOf(film.getReleaseDate()));
            preparedStatement.setLong(4, film.getDuration());
            preparedStatement.setLong(5, film.getMpa().getId());
            preparedStatement.setInt(6, film.getId());
            int rows = preparedStatement.executeUpdate();
            if (rows == 0) {
                log.trace("Wrong film id. Throwing eception");
                throw new FilmNotFoundException("No film with id - " + film.getId());
            }
            genreStorage.saveFilmGenres(film);
            film.setGenres(genreStorage.getFilmGenres(film.getId()));
            log.trace("Succesfully updated film! " + film);
        } catch (SQLException e) {
            log.error("Failed delete film due to: " + e.getClass());
            throw new DatabaseConnectionException("Failed to delete film");
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
                        resultSet.getLong(5),
                        mpaStorage.getMPA(resultSet.getInt(6)).get(),
                        genreStorage.getFilmGenres(resultSet.getInt(1))
                );
                for (Integer userId : getFilmLikes(returnFilm.getId())) {
                    returnFilm.likeFilm(userId);
                }

            }
            return returnFilm;
        } catch (SQLException e) {
            log.error("Failed getting film due to: " + e.getClass());
            throw new DatabaseConnectionException("Failed to get film");
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
                        resultSet.getLong(5),
                        mpaStorage.getMPA(resultSet.getInt(6)).get(),
                        genreStorage.getFilmGenres(resultSet.getInt(1))
                );
                for (Integer userId : getFilmLikes(filmToAdd.getId())) {
                    filmToAdd.likeFilm(userId);
                }
                allFilms.add(filmToAdd);
            }
            return allFilms;
        } catch (SQLException e) {
            log.error("Failed get films friends due to: " + e.getClass());
            throw new DatabaseConnectionException("Failed to get all films film");
        }
    }

    @Override
    public void saveLike(int userId, int filmId) {
        log.trace("Level: Storage. Method: saveLike.");
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
            log.trace("User "  + userId + " like to film " + filmId + " saved");
        } catch (SQLException e) {
            log.error("Failed get all users due to: " + e.getClass());
            throw new DatabaseConnectionException("Failed to save liked film " + filmId + " by user " + userId);
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
            if (rows == 0) {
                log.trace("Wrong user id. Throwing eception");
                throw new FilmNotFoundException("Unable to erase friendship beetween users");
            }
        } catch (SQLException e) {
            log.error("Failed get films friends due to: " + e.getClass());
            throw new DatabaseConnectionException("Failed to save liked film " + filmId + " by user " + userId);
        }
    }

    @Override
    public List<Film> getMostLikedFilms(int amount) {
        log.trace("Level: Storage. Method: getMostLikedFilms.");
        try (
                Connection connection = DriverManager.getConnection(
                        params.getUrl(),
                        params.getUserName(),
                        params.getPassword()
                );
                PreparedStatement preparedStatement = connection.prepareStatement(
                        "SELECT f.*, COUNT(l.USER_ID) AS AMOUNT\n" +
                                "FROM FILMS f \n" +
                                "LEFT JOIN LIKES l\n" +
                                "\tON f.ID = l.FILM_ID \n" +
                                "GROUP BY f.ID, f.NAME, f.DESCRIPTION, f.RELEASE_DATE, f.DURATION, f.MPA_ID \n" +
                                "ORDER BY AMOUNT DESC, f.NAME ASC\n" +
                                "LIMIT ?",
                        Statement.RETURN_GENERATED_KEYS);
        ) {
            log.debug("Succesfully establihed connection to databae - " + params.getUrl());
            List<Film> mostLikedFilms = new ArrayList<>();
            preparedStatement.setInt(1, amount);
            ResultSet resultSet = preparedStatement.executeQuery();
            log.trace("Got " + resultSet.getFetchSize() + " popular films.");
            while (resultSet.next()) {
                Film filmToAdd = new Film(
                        resultSet.getInt(1),
                        resultSet.getString(2),
                        resultSet.getString(3),
                        resultSet.getDate(4).toLocalDate(),
                        resultSet.getLong(5),
                        mpaStorage.getMPA(resultSet.getInt(6)).get(),
                        genreStorage.getFilmGenres(resultSet.getInt(1))
                );
                for (Integer userId : getFilmLikes(filmToAdd.getId())) {
                    filmToAdd.likeFilm(userId);
                }
                mostLikedFilms.add(filmToAdd);
            }
            return mostLikedFilms;
        } catch (SQLException e) {
            log.error("Failed get films friends due to: " + e.getClass());
            throw new DatabaseConnectionException("Failed to get most popular films");
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
            throw new DatabaseConnectionException("Failed to get all films film");
        }
    }
}
