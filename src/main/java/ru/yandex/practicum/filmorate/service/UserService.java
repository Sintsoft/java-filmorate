package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.utility.exceptions.DatabaseConnectionEхception;
import ru.yandex.practicum.filmorate.utility.exceptions.EntityValidationException;
import ru.yandex.practicum.filmorate.utility.exceptions.IncorrectEntityIDException;
import ru.yandex.practicum.filmorate.utility.exceptions.UserNotFoundException;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    @Autowired
    private final UserStorage userStorage;

    public User addUser(@Valid User user) {
        log.trace("Level: Service. Method: addUser. Input: " + user);
        if (user == null) {
            log.debug("Got null user");
            throw new EntityValidationException("Got null user");
        }
        log.trace("User is correct!");
        checkAndUpdateUserName(user);
        userStorage.addUser(user);
        return user;
    }

    public User getUserById(int id) {
        log.trace("Level: Service. Method: getUserById. Input: " + id);
        User user = userStorage.getUser(id);
        if (user == null) {
            log.warn("User with id " + id + " not found.");
            throw new UserNotFoundException("User with id " + id + " not found.");
        }
        return user;
    }

    public List<User> getAllUsers() {
        log.trace("Level: Service. Method: getUserById.");
        return userStorage.getAllUsers();
    }

    public User updateUser(@Valid User user) {
        log.trace("Level: Service. Method: updateUser. Input: " + user);
        if (user == null) {
            log.debug("Got null user");
            throw new EntityValidationException("Got null user");
        }
        userStorage.updateUser(user);
        return user;
    }

    public User deleteUser(@Valid User user) {
        log.trace("Level: Service. Method: updateUser. Input: " + user);
        if (user == null) {
            log.debug("Got null user");
            throw new EntityValidationException("Got null user");
        }
        userStorage.deleteUser(user);
        return user;
    }

    public void setFriend(int userId, int friendId) {
        log.trace("Level: Service. Method: setFriend. Input: " + userId + " " + friendId);
        if (userId == friendId) {
            log.warn("Attempting to user friendship with itself.");
            throw new IncorrectEntityIDException("Attempting to user friendship with itself.");
        }
        try {
            userStorage.saveFriendShip(userId, friendId);
        } catch (Exception e) {
            throw new UserNotFoundException("Sent correct user's and friend's ids");
        }
    }

    public void unsetFriend(int userId, int friendId) {
        log.trace("Level: Service. Method: unsetFriend. Input: " + userId + " " + friendId);
        if (userId == friendId) {
            log.warn("Attempting to user friendship with itself.");
            throw new UserNotFoundException("Attempting to user friendship with itself.");
        }
        try {
            userStorage.eraseFrienShip(userId, friendId);
        } catch (Exception e) {
            throw new IncorrectEntityIDException("Sent correct user's and friend's ids");
        }
    }

    public List<User> getUserFriends(int userId) {
        List<User> friends = new ArrayList<>();
        for (Integer friendId : getUserById(userId).getFriends()) {
            friends.add(getUserById(friendId));
        }
        return friends;
    }

    public List<User> getUsersCommonFriends(int userId, int otherId) {
        log.trace("Level: Service. Method: unsetFriend. Input: " + userId + " " + otherId);
        return userStorage.getCommonFriends(userId, otherId);
    }


    private User checkAndUpdateUserName(User user) {
        log.trace("Level: Service. Method: checkAndUpdateUserName. Input: " + user);
        if (user.getName() == null || user.getName().isBlank()) {
            log.trace("Get blank user name in user " + user.getId());
            user.setName(user.getLogin());
        }
        return user;
    }

}
