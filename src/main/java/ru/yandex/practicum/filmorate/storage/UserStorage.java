package ru.yandex.practicum.filmorate.storage;

import java.util.List;

import ru.yandex.practicum.filmorate.model.User;

public interface UserStorage {
    

    void addUser(User user);

    void deleteUser(User user);

    void updateUser(User user);

    User getUser(int id);

    List<User> getAllUsers();

}
