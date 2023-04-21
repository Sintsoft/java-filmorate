package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.utility.exceptions.IncorrectEntityIDException;
import ru.yandex.practicum.filmorate.utility.exceptions.UserNotFoundException;

import javax.validation.Valid;
import java.util.*;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {

    Map<Integer, User> inMemoryStorage = new TreeMap<>();
    int userIdIterator = 1;

    @Override
    public void addUser(@Valid User user) {
        log.debug("adding User - " + user);
        if (user.getId() != 0) {
            log.trace("Added user have incorrect id - " + user.getId());
            throw new IncorrectEntityIDException("Wrong method! User with id should be updated");
        } else {
            user.setId(userIdIterator);
            log.trace("Set added user id - " + userIdIterator);
            inMemoryStorage.put(user.getId(), user);
            log.trace("Added user to storage. User id = " + user.getId());
            userIdIterator++;
            log.trace("Next user id will be = " + userIdIterator);
        }
    }

    @Override
    public void deleteUser(@Valid User user) {
        log.debug("Deleting user - " + user);
        if (!inMemoryStorage.containsKey(user.getId())) {
            log.trace("Wrong user id. Throwing eception");
            throw new UserNotFoundException("No user with id - " + user.getId());
        } else {
            inMemoryStorage.remove(user.getId());
            log.trace("Succesfully removed user!");
        }
    }

    @Override
    public void updateUser(@Valid User user) {
        log.debug("Updating user - " + user);
        if (!inMemoryStorage.containsKey(user.getId())) {
            log.trace("Wrong user id. Throwing eception");
            throw new UserNotFoundException("No user with id - " + user.getId());
        } else {
            inMemoryStorage.put(user.getId(), user);
            log.trace("Succesfully removed user!");
        }
    }

    @Override
    public User getUser(int id) {
        return inMemoryStorage.get(id);
    }

    @Override
    public List<User> getAllUsers() {
        return List.copyOf(inMemoryStorage.values());
    }

    @Override
    public void saveFriendShip(int userId, int friendId) {
        if (!inMemoryStorage.containsKey(userId) || !inMemoryStorage.containsKey(friendId)) {
            throw new UserNotFoundException("Incorrect user ID");
        }
        inMemoryStorage.get(userId).addFriend(friendId);
    }

    @Override
    public void eraseFrienShip(int userId, int friendId) {
        if (!inMemoryStorage.containsKey(userId) || !inMemoryStorage.containsKey(friendId)) {
            throw new UserNotFoundException("Incorrect user ID");
        }
        inMemoryStorage.get(userId).removeFriend(friendId);
    }

    @Override
    public List<User> getCommonFriends(int userid, int otherId) {
        List<User> commonFriends = new ArrayList<>();
        Set<Integer> userfriends = inMemoryStorage.get(userid).getFriends();
        for (Integer othersFriendId : inMemoryStorage.get(otherId).getFriends()) {
            if (userfriends.contains(othersFriendId)) {
                commonFriends.add(inMemoryStorage.get(otherId));
            }
        }
        return commonFriends;
    }


}
