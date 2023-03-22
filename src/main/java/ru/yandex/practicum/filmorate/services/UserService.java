package ru.yandex.practicum.filmorate.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.utility.exceptions.UserNotFoundException;

@Service
@Slf4j
public class UserService {
    
    @Autowired
    private UserStorage storage;
    private int userIdIterator = 1;

    public User addUser(User user) {
        userNameCheck(user);
        user.setId(userIdIterator);
        storage.addUser(user);
        userIdIterator++; // Итерируем после того как успешно добавили
        log.trace("User itreator afer add new user = " + userIdIterator);
        return user;
    }

    public User getUser(int id) {
        User user = storage.getUser(id);
        if (user == null) {
            throw new UserNotFoundException("Can't found user with id: " + id );
        }
        return user;
    }

    public List<User> getAllUsers() {
        return storage.getAllUsers();
    }

    public List<User> getUserFriends(int userId) {
        User user = storage.getUser(userId);
        if (user == null) {
            throw new UserNotFoundException("Can't find user to make friends");
        }
        List<User> friends = new ArrayList<>();
        for(Integer id : user.getFriends()) {
            if (storage.getUser(id) != null) {
                friends.add(storage.getUser(id));
            }
        }
        return friends;
    }

    public User updateUser(User user) {
        log.trace("Updating user with id = " + user.getId());
        storage.updateUser(user);
        return user;
    }

    public void setFriends(int userId, int friendId) {
        User user = storage.getUser(userId);
        User friend = storage.getUser(friendId);
        if (user == null || friend == null) {
            throw new UserNotFoundException("Can't find user to make friends");
        }
        user.addFriend(friend);
        friend.addFriend(user);
    }

    public void unfriendUsers(int userId, int friendId) {
        User user = storage.getUser(userId);
        User friend = storage.getUser(friendId);
        if (user == null || friend == null) {
            throw new UserNotFoundException("Can't find user to make friends");
        }
        user.removeFriend(friend);
        friend.removeFriend(user);
    }

    private User userNameCheck(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        return user;
    }

    public List<User> getUsersCommonFriends(int user1Id, int user2Id) {
        User user1 = storage.getUser(user1Id);
        User user2 = storage.getUser(user2Id);
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
        return commonFriends;
    }

}
