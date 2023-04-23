package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.utility.DataBaseConnectionParams;
import ru.yandex.practicum.filmorate.utility.exceptions.DatabaseConnectionException;
import ru.yandex.practicum.filmorate.utility.exceptions.IncorrectEntityIDException;
import ru.yandex.practicum.filmorate.utility.exceptions.UserNotFoundException;

import javax.sql.DataSource;
import javax.validation.Valid;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@Slf4j
@Primary

public class DbUserStorage implements UserStorage {

    private String USER_INSERT_QUERY = "INSERT INTO USERS (USERNAME, LOGIN, EMAIL, BIRTHDAY) " +
            "VALUES (?, ?, ?, ?)";

    @Autowired
    DataBaseConnectionParams params;

    @Autowired
    DataSource connection;


    @Override
    public void addUser(@Valid User user) {
        log.debug("Calling addUser");
        JdbcTemplate jdbc = new JdbcTemplate(this.connection);
        if (user.getId() != 0) {
            log.trace("Added user have incorrect id - " + user.getId());
            throw new IncorrectEntityIDException("Wrong method! User with id should be equal to 0");
        }
        jdbc.update(USER_INSERT_QUERY,
                user.getName(),
                user.getLogin(),
                user.getEmail(),
                Date.valueOf(user.getBirthday())
                );
        log.error("USER GOT ID " + user.getId());
    }

    @Override
    public void deleteUser(User user) {
        log.debug("Deleting user - " + user);
        try (
                Connection connection = DriverManager.getConnection(
                        params.getUrl(),
                        params.getUserName(),
                        params.getPassword()
                );
                PreparedStatement preparedStatement = connection.prepareStatement(
                        "DELETE FROM USERS WHERE ID = ?",
                        Statement.RETURN_GENERATED_KEYS);
        ) {
            log.debug("Succesfully establihed connection to databae - " + params.getUrl());
            preparedStatement.setInt(1, user.getId());
            int rows = preparedStatement.executeUpdate();
            if (rows == 0) {
                log.trace("Wrong user id. Throwing eception");
                throw new UserNotFoundException("No user with id - " + user.getId());
            }
            log.trace("Succesfully removed user!");
        } catch (SQLException e) {
            log.error("Failed delete user due to: " + e.getClass());
            throw new DatabaseConnectionException("Failed to delete user");
        }
    }

    @Override
    public void updateUser(User user) {
        log.debug("Updating user - " + user);
        try (
                Connection connection = DriverManager.getConnection(
                        params.getUrl(),
                        params.getUserName(),
                        params.getPassword()
                );
                PreparedStatement preparedStatement = connection.prepareStatement(
                        "UPDATE USERS SET USERNAME = ?, LOGIN = ?, EMAIL = ?, BIRTHDAY = ? WHERE ID = ?",
                        Statement.RETURN_GENERATED_KEYS);
        ) {
            log.debug("Succesfully establihed connection to databae - " + params.getUrl());
            preparedStatement.setString(1, user.getName());
            preparedStatement.setString(2, user.getLogin());
            preparedStatement.setString(3, user.getEmail());
            preparedStatement.setDate(4, Date.valueOf(user.getBirthday()));
            preparedStatement.setInt(5, user.getId());
            int rows = preparedStatement.executeUpdate();
            if (rows == 0) {
                log.trace("Wrong user id. Throwing eception");
                throw new UserNotFoundException("No user with id - " + user.getId());
            }
            log.trace("Succesfully removed user!");
        } catch (SQLException e) {
            log.error("Failed delete user due to: " + e.getClass());
            throw new DatabaseConnectionException("Failed to delete user");
        }
    }

    @Override
    public User getUser(int id) {
        log.debug("Calling getUser");
        try (
                Connection connection = DriverManager.getConnection(
                        params.getUrl(),
                        params.getUserName(),
                        params.getPassword()
                );
                PreparedStatement preparedStatement = connection.prepareStatement(
                        "SELECT * FROM USERS WHERE id = ?",
                        Statement.RETURN_GENERATED_KEYS);
        ) {
            log.debug("Succesfully establihed connection to databae - " + params.getUrl());
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            User returnUser = null;
            while (resultSet.next()) {
                returnUser = new User(
                        resultSet.getInt(1),
                        resultSet.getString(2),
                        resultSet.getString(3),
                        resultSet.getString(4),
                        resultSet.getDate(5).toLocalDate()
                );
                for (Integer friendId : this.getUserFriends(returnUser.getId())) {
                    returnUser.addFriend(friendId);
                }
            }
            return returnUser;
        } catch (SQLException e) {
            log.error("Failed getting user due to: " + e.getClass());
            throw new DatabaseConnectionException("Failed to get user");
        }
    }

    @Override
    public List<User> getAllUsers() {
        List<User> allUsers = new ArrayList<>();
        log.debug("Calling getAllUsers");
        try (
                Connection connection = DriverManager.getConnection(
                        params.getUrl(),
                        params.getUserName(),
                        params.getPassword()
                );
                PreparedStatement preparedStatement = connection.prepareStatement(
                        "SELECT * FROM USERS",
                        Statement.RETURN_GENERATED_KEYS);
        ) {
            log.debug("Succesfully establihed connection to databae - " + params.getUrl());
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                User userToAdd = new User(
                        resultSet.getInt(1),
                        resultSet.getString(2),
                        resultSet.getString(3),
                        resultSet.getString(4),
                        resultSet.getDate(5).toLocalDate()
                );
                for (Integer id : this.getUserFriends(userToAdd.getId())) {
                    userToAdd.addFriend(id);
                }
                allUsers.add(userToAdd);
            }
            return allUsers;
        } catch (SQLException e) {
            log.error("Failed get all users due to: " + e.getClass());
            throw new DatabaseConnectionException("Failed to get all users user");
        }
    }

    @Override
    public void saveFriendShip(int userId, int friendId) {
        try (
                Connection connection = DriverManager.getConnection(
                        params.getUrl(),
                        params.getUserName(),
                        params.getPassword()
                );
                PreparedStatement preparedStatement = connection.prepareStatement(
                        "INSERT INTO FRIENDS (REQUESTER_ID, ACCEPTER_ID) VALUES (?, ?)",
                        Statement.RETURN_GENERATED_KEYS);
        ) {
            log.debug("Succesfully establihed connection to databae - " + params.getUrl());
            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, friendId);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            log.error("Failed get all users due to: " + e.getClass());
            throw new DatabaseConnectionException("Failed to add firend " + friendId + " to user " + userId);
        }
    }

    @Override
    public void eraseFrienShip(int userId, int friendId) {
        try (
                Connection connection = DriverManager.getConnection(
                        params.getUrl(),
                        params.getUserName(),
                        params.getPassword()
                );
                PreparedStatement preparedStatement = connection.prepareStatement(
                        "DELETE FROM FRIENDS WHERE REQUESTER_ID = ? AND ACCEPTER_ID = ?",
                        Statement.RETURN_GENERATED_KEYS);
        ) {
            log.debug("Succesfully establihed connection to databae - " + params.getUrl());
            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, friendId);
            int rows = preparedStatement.executeUpdate();
            if (rows == 0) {
                log.trace("Wrong user id. Throwing eception");
                throw new UserNotFoundException("Unable to erase friendship beetween users");
            }
        } catch (SQLException e) {
            log.error("Failed get all users due to: " + e.getClass());
            throw new DatabaseConnectionException("Failed to add firend " + friendId + " to user " + userId);
        }
    }

    @Override
    public List<User> getCommonFriends(int userid, int otherId) {
        List<User> allUsers = new ArrayList<>();
        log.debug("Calling getCommonFriends");
        try (
                Connection connection = DriverManager.getConnection(
                        params.getUrl(),
                        params.getUserName(),
                        params.getPassword()
                );
                PreparedStatement preparedStatement = connection.prepareStatement(
                        "SELECT DISTINCT f1.ACCEPTER_ID \n" +
                                "FROM FRIENDS f1\n" +
                                "INNER JOIN FRIENDS f2\n" +
                                "\tON f1.ACCEPTER_ID = f2.ACCEPTER_ID AND f1.REQUESTER_ID != f2.REQUESTER_ID \n" +
                                "WHERE f1.REQUESTER_ID = ? AND f2.REQUESTER_ID = ?",
                        Statement.RETURN_GENERATED_KEYS);
        ) {
            log.debug("Succesfully establihed connection to databae - " + params.getUrl());
            preparedStatement.setInt(1, userid);
            preparedStatement.setInt(2, otherId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                User userToAdd = this.getUser(resultSet.getInt(1));
                for (Integer id : this.getUserFriends(userToAdd.getId())) {
                    userToAdd.addFriend(id);
                }
                allUsers.add(userToAdd);
            }
            return allUsers;
        } catch (SQLException e) {
            log.error("Failed get all users due to: " + e.getClass());
            throw new DatabaseConnectionException("Failed to get all users user");
        }
    }

    private Set<Integer> getUserFriends(int userId) {
        Set<Integer> friendsSet = new HashSet<>();
        log.debug("Calling getAllUsers");
        try (
                Connection connection = DriverManager.getConnection(
                        params.getUrl(),
                        params.getUserName(),
                        params.getPassword()
                );
                PreparedStatement preparedStatement = connection.prepareStatement(
                        "SELECT ACCEPTER_ID FROM FRIENDS WHERE REQUESTER_ID = ?",
                        Statement.RETURN_GENERATED_KEYS);
        ) {
            log.debug("Succesfully establihed connection to databae - " + params.getUrl());
            preparedStatement.setInt(1, userId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                friendsSet.add(
                        resultSet.getInt(1)
                );
            }
            return friendsSet;
        } catch (SQLException e) {
            log.error("Failed get users friends due to: " + e.getClass());
            throw new DatabaseConnectionException("Failed to get all users user");
        }
    }
}
