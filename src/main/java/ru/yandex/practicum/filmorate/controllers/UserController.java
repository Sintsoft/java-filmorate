package ru.yandex.practicum.filmorate.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.utility.exceptions.ValidationException;

@RestController
@Slf4j
@RequestMapping("/users")
public class UserController {

    private Map<Integer, User> users = new HashMap<>();
    private int userIdIterator = 1;;

    @GetMapping
    public List<User> getAllUsers() {
        log.trace("Call /users GET request");
        return List.copyOf(users.values());
    }
    
    @PostMapping
    public User creatUser(@Valid @RequestBody User user) {
        log.trace("Call /users POST request");
        if (user == null) {
            log.info("Null user body");
            throw new ValidationException();
        }
        if (user.getId() != 0) {
            log.info("Wrong user add method");
            throw new ValidationException("Wrong method");
        }
        user = userNameCheck(user);
        user.setId(userIdIterator++);
        users.put(user.getId(), user);
        return user;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        log.trace("Call /users POST request");
        if (user == null) {
            log.info("Null user body");
            throw new ValidationException();
        }
        if (!users.containsKey(user.getId())) {
            log.info("Wrong id method");
            throw new ValidationException("Wrong method");
        }
        user = userNameCheck(user);
        users.put(user.getId(), user);
        return user;
    }

    private User userNameCheck(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        return user;
    }
}
