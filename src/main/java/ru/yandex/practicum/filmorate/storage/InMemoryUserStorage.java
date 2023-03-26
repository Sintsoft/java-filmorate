package ru.yandex.practicum.filmorate.storage;

import java.util.HashMap;
import java.util.Map;

import javax.validation.ValidationException;

import java.util.List;

import org.springframework.stereotype.Component;

import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.utility.exceptions.UserNotFoundException;

@Component
public class InMemoryUserStorage implements UserStorage {

    private final Map<Integer, User> users = new HashMap<>();

    @Override
    public void addUser(User user) {
        if (!users.containsKey(user.getId()) && user.getId() > 0) {
            users.put(
                user.getId(), user
            );
        } else {
            throw new ValidationException("Add user failed - invaild user");
        }
    }

    @Override
    public void deleteUser(User user) {
        if (users.containsKey(user.getId())) {
            users.remove(user.getId());
        } else {
            throw new ValidationException("Wrong user to delete");
        }
    }

    @Override
    public void updateUser(User user) {
        if (users.containsKey(user.getId())) {
            users.put(
                user.getId(), user
            );
        } else {
            throw new UserNotFoundException("Update canceled. Can't find user with id: " + user.getId());
        }
    }

    @Override
    public User getUser(int id) {
        return users.get(id);
    }

    @Override
    public List<User> getAllUsers() {
        return List.copyOf(users.values());
    }
    
}
