package ru.yandex.practicum.filmorate.storage;

import java.util.HashMap;
import java.util.Map;

import javax.validation.ValidationException;

import java.util.List;

import org.springframework.stereotype.Component;

import ru.yandex.practicum.filmorate.model.User;

@Component
public class InMemoryUserStorage implements UserStorage {

    Map<Integer, User> users = new HashMap<>();

    @Override
    public void addUser(User user) {
        if (user.getId() > 0) {
            users.put(
                user.getId(), user
            );
        } else {

        }
    }

    @Override
    public void deleteUser(User user) {
        if (users.containsKey(user.getId())) {
            users.remove(user.getId());
        }
    }

    @Override
    public void updateUser(User user) {
        if (users.containsKey(user.getId())) {
            users.put(
                user.getId(), user
            );
        } else {
            throw new ValidationException("Update canceled. Can't find user with id: " + user.getId());
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
