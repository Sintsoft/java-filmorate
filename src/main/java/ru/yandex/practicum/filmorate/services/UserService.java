package ru.yandex.practicum.filmorate.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

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

    public List<User> getAllUsers() {
        return storage.getAllUsers();
    }

    public User updateUser(User user) {
        log.trace("Updating user with id = " + user.getId());
        storage.updateUser(user);
        return user;
    }

    private User userNameCheck(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        return user;
    }

}
