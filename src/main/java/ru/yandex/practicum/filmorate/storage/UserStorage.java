package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {

    void addUser(User user);

    void deleteUser(User user);

    void updateUser(User user);

    User getUser(int id);

    List<User> getAllUsers();

    void saveFriendShip(int userId, int friendId);

    void eraseFrienShip(int userId, int friendId);

    List<User> getCommonFriends(int userid, int otherId);

}

