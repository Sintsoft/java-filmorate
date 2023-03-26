package ru.yandex.practicum.filmorate.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.utility.exceptions.EntityValidationException;
import ru.yandex.practicum.filmorate.utility.exceptions.UserNotFoundException;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    @Autowired
    private final UserStorage storage;
    private int userIdIterator = 1;

    public User addUser(User user) {
        log.trace("call addUser method with " + user);
        if (user == null) {
            log.debug("Got null user");
            throw new EntityValidationException("Got null user");
        }
        chaeckAndUpdateUserName(user);
        user.setId(userIdIterator);
        storage.addUser(user);
        userIdIterator++; // Итерируем после того как успешно добавили
        log.trace("User itreator afer add new user = " + userIdIterator);
        return user;
    }

    public User getUser(int id) {
        log.trace("call getUser method with " + id);
        User user = storage.getUser(id);
        log.trace("User with id = " + id + " is " + user);
        if (user == null) {
            log.debug("Got null user");
            throw new UserNotFoundException("Can't found user with id: " + id);
        }
        return user;
    }

    public List<User> getAllUsers() {
        log.trace("call getAllUsers method");
        return storage.getAllUsers();
    }

    public List<User> getUserFriends(int userId) {
        log.trace("call updateUser method with " + userId);
        User user = storage.getUser(userId);
        log.trace("User with id = " + userId + " is " + user);
        if (user == null) {
            log.debug("Got null user");
            throw new UserNotFoundException("Can't find user to make friends");
        }
        List<User> friends = new ArrayList<>();
        for (Integer id : user.getFriends()) {
            if (storage.getUser(id) != null) {
                friends.add(storage.getUser(id));
            }
        }
        return friends;
    }

    public User updateUser(User user) {
        log.trace("call updateUser method with " + user);
        if (user == null) {
            log.debug("Got null user");
            throw new EntityValidationException("Got null user");
        }
        chaeckAndUpdateUserName(user);
        storage.updateUser(user);
        return user;
    }

    public void setFriends(int userId, int friendId) {
        log.trace("call setFriends method with user " + userId + " and friend " + friendId);
        User user = storage.getUser(userId);
        log.trace("User with id = " + userId + " is " + user);
        User friend = storage.getUser(friendId);
        log.trace("User with id = " + friendId + " is " + friend);
        if (user == null || friend == null) {
            log.debug("Got null user or friend");
            throw new UserNotFoundException("Can't find user to make friends");
        }
        user.addFriend(friend);
        friend.addFriend(user);
    }

    public void unfriendUsers(int userId, int friendId) {
        log.trace("call unfriendUsers method with user " + userId + " and friend " + friendId);
        User user = storage.getUser(userId);
        log.trace("User with id = " + userId + " is " + user);
        User friend = storage.getUser(friendId);
        log.trace("User with id = " + friendId + " is " + friend);
        if (user == null || friend == null) {
            throw new UserNotFoundException("Can't find user to make friends");
        }
        user.removeFriend(friend);
        friend.removeFriend(user);
    }

    private User chaeckAndUpdateUserName(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            log.trace("Get blank user name in user " + user.getId());
            user.setName(user.getLogin());
        }
        return user;
    }

    public List<User> getUsersCommonFriends(int user1Id, int user2Id) {
        log.trace("call unfriendUsers method with user " + user1Id + " and friend " + user2Id);
        User user1 = storage.getUser(user1Id);
        log.trace("User with id = " + user1Id + " is " + user1);
        User user2 = storage.getUser(user2Id);
        log.trace("User with id = " + user1Id + " is " + user2);
        Set<Integer> commonFriendsIDs = user1
            .getFriends()
            .stream()
            .distinct()
            .filter(user2.getFriends()::contains)
            .collect(Collectors.toSet());
        List<User> commonFriends = new ArrayList<>();
        for (Integer id : commonFriendsIDs) {
            commonFriends.add(storage.getUser(id));
        }
        log.trace("Amount of common friends beetween users " + user1Id + " and " + user2Id + " is " + commonFriends.size());
        return commonFriends;
    }

}
