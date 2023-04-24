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
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.utility.exceptions.IncorrectEntityIDException;
import ru.yandex.practicum.filmorate.utility.exceptions.UserNotFoundException;

import javax.validation.Valid;
import java.sql.*;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

@Component
@Slf4j
@Primary
@RequiredArgsConstructor
public class DbUserStorage implements UserStorage {

    private static final String INSERT_USER_QUERY = "INSERT INTO USERS (USERNAME, LOGIN, EMAIL, BIRTHDAY) " +
            "VALUES (?, ?, ?, ?)";
    private static final String DELETE_USER_QUERY = "DELETE FROM USERS WHERE ID = ?";
    private static final String UPDATE_USER_QUERY = "UPDATE USERS SET " +
            "USERNAME = ?, LOGIN = ?, EMAIL = ?, BIRTHDAY = ? WHERE ID = ?";
    private static final String GET_USER_QUERY = "SELECT * FROM USERS WHERE id = ?";
    private static final String GET_ALL_USERS_QUERY = "SELECT * FROM USERS";
    private static final String GET_USER_FRIENDS_QUERY = "SELECT ACCEPTER_ID FROM FRIENDS WHERE REQUESTER_ID = ?";
    private static final String GET_COMMON_USERS_FRIENS_QUERY = "SELECT * FROM USERS WHERE ID IN (" +
            "SELECT DISTINCT f1.ACCEPTER_ID " +
            "FROM FRIENDS f1 " +
            "INNER JOIN FRIENDS f2 " +
            "ON f1.ACCEPTER_ID = f2.ACCEPTER_ID AND f1.REQUESTER_ID != f2.REQUESTER_ID " +
            "WHERE f1.REQUESTER_ID = ? AND f2.REQUESTER_ID = ?)";
    private static final String INSERT_FRIENDSHIP_QUERY = "INSERT INTO FRIENDS (REQUESTER_ID, ACCEPTER_ID) " +
            "VALUES (?, ?)";
    private static final String DELETE_FRIENDSHIP_QUERY = "DELETE FROM FRIENDS " +
            "WHERE REQUESTER_ID = ? AND ACCEPTER_ID = ?";

    @Autowired
    private final JdbcTemplate jdbc;


    @Override
    public void addUser(@Valid User user) {
        log.debug("Calling addUser");
        KeyHolder keyHolder = new GeneratedKeyHolder();
        if (user.getId() != 0) {
            log.trace("Added user have incorrect id - " + user.getId());
            throw new IncorrectEntityIDException("Wrong method! User with id should be equal to 0");
        }
        jdbc.update(connection -> {
                    PreparedStatement ps = connection.prepareStatement(INSERT_USER_QUERY,
                            Statement.RETURN_GENERATED_KEYS);
                    ps.setString(1, user.getName());
                    ps.setString(2, user.getLogin());
                    ps.setString(3, user.getEmail());
                    ps.setDate(4, Date.valueOf(user.getBirthday()));
                    return ps;
                }, keyHolder);
        user.setId(keyHolder.getKey().intValue());
    }

    @Override
    public void deleteUser(User user) {
        log.debug("Deleting user - " + user);
        int result = jdbc.update(DELETE_USER_QUERY, user.getId());
        if (result == 0) {
            log.info("Deletion failed. No user with id: " + user.getId());
            throw new UserNotFoundException("User with id "
                    + user.getId() + "was not found.");
        }
    }

    @Override
    public void updateUser(User user) {
        log.debug("Updating user - " + user);
        int result = jdbc.update(UPDATE_USER_QUERY,
                user.getName(),
                user.getLogin(),
                user.getEmail(),
                Date.valueOf(user.getBirthday()),
                user.getId());
        if (result == 0) {
            log.info("Deletion failed. No user with id: " + user.getId());
            throw new UserNotFoundException("User with id "
                    + user.getId() + "was not found.");
        }
    }

    @Override
    public User getUser(int id) {
        log.debug("Calling getUser");
        User user = null;
        try {
            user = jdbc.queryForObject(
                    GET_USER_QUERY,
                    new Object[]{id},
                    new UserRowMapper()
            );
            for (Integer friendId : this.getUserFriends(user.getId())) {
                user.addFriend(friendId);
            }
        } catch (EmptyResultDataAccessException e) {
            log.info("User with id = " + id + " not found.");
        }
        return user;
    }

    @Override
    public List<User> getAllUsers() {
        log.debug("Calling getAllUsers");
        List<User> allUsers = jdbc.query(
                GET_ALL_USERS_QUERY,
                new UserRowMapper());
        for (User user : allUsers) {
            for (Integer id : this.getUserFriends(user.getId())) {
                user.addFriend(id);
            }
        }
        return allUsers;

    }

    @Override
    public void saveFriendShip(int userId, int friendId) {
        jdbc.update(INSERT_FRIENDSHIP_QUERY, userId, friendId);
    }

    @Override
    public void eraseFrienShip(int userId, int friendId) {
        int result = jdbc.update(DELETE_FRIENDSHIP_QUERY, userId, friendId);
        if (result == 0) {
            log.info("Deletion failed.");
            throw new UserNotFoundException("Failed to delete friendship beetwen "
                    + userId + " and " + friendId);
        }
    }

    @Override
    public List<User> getCommonFriends(int userid, int otherId) {
        log.debug("Calling getCommonFriends");
        List<User> commonFriends = jdbc.query(
                GET_COMMON_USERS_FRIENS_QUERY,
                new Object[]{userid, otherId},
                new UserRowMapper());
        for (User user : commonFriends) {
            for (Integer id : this.getUserFriends(user.getId())) {
                user.addFriend(id);
            }
        }
        return commonFriends;
    }

    private Set<Integer> getUserFriends(int userId) {
        return new TreeSet<>(
                jdbc.queryForList(
                        GET_USER_FRIENDS_QUERY,
                        new Object[]{userId},
                        Integer.class)
        );
    }

    private static class UserRowMapper implements RowMapper<User> {

        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new User(
                    rs.getInt(1),
                    rs.getString(2),
                    rs.getString(3),
                    rs.getString(4),
                    rs.getDate(5).toLocalDate()
            );
        }
    }

}
