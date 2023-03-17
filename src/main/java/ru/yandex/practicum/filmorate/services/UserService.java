package ru.yandex.practicum.filmorate.services;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

@Service
@Slf4j
public class UserService {
    
    private UserStorage storage = new InMemoryUserStorage();
    private int userIdIterator = 1;

    public User addUser(User user) {
        userNameCheck(user);
        user.setId(userIdIterator++);
        log.trace("User itreator afer add new user = " + userIdIterator);
        storage.addUser(user);
        return user;
    }

    public List<User> getAllUsers() {
        return storage.getAllUsers();
    }

    public User updatUser(User user) {
        log.trace("Updating user with id = " + userIdIterator);
        
        return user;
    }

    private User userNameCheck(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        return user;
    }

}
