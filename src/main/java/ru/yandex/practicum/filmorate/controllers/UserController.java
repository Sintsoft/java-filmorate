package ru.yandex.practicum.filmorate.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.services.UserService;
import ru.yandex.practicum.filmorate.utility.exceptions.ValidationException;

@RestController
@Slf4j
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public List<User> getAllUsers() {
        log.trace("Call /users GET request");
        return List.of();
    }
    
    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        log.trace("Call /users POST request");
        if (user == null) {
            log.info("Null user body");
            throw new ValidationException();
        }
        // if (users.containsKey(user.getId())) {
        //     log.info("Wrong user add method");
        //     throw new ValidationException("Wrong method");
        // }
        return userService.addUser(user);
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        log.trace("Call /users POST request");
        if (user == null) {
            log.info("Null user body");
            throw new ValidationException();
        }
        // if (!users.containsKey(user.getId())) {
        //     log.info("Wrong id method");
        //     throw new ValidationException("Wrong method");
        // }
        return userService.addUser(user);
    }

}
