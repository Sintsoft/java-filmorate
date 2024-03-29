package ru.yandex.practicum.filmorate.controller;

import java.util.List;
import javax.validation.Valid;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

@RestController
@Slf4j
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    @Autowired
    private final UserService userService;

    @GetMapping
    public List<User> getAllUsers() {
        log.trace("Call /users GET request");
        return userService.getAllUsers();
    }

    @GetMapping("/{userId}")
    public User getUser(@PathVariable int userId) {
        return userService.getUserById(userId);
    }

    @GetMapping("/{userId}/friends")
    public List<User> getUserFriends(@PathVariable int userId) {
        return userService.getUserFriends(userId);
    }

    @PutMapping("/{userId}/friends/{friendId}")
    public void addUserFriend(@PathVariable int userId, @PathVariable int friendId) {
        userService.setFriend(userId, friendId);
    }

    @DeleteMapping("/{userId}/friends/{friendId}")
    public void unfriendUsers(@PathVariable int userId, @PathVariable int friendId) {
        userService.unsetFriend(userId, friendId);
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        log.trace("Call /users POST request");
        return userService.addUser(user);
    }

    @GetMapping("/{userId}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable int userId, @PathVariable int otherId) {
        return userService.getUsersCommonFriends(userId, otherId);
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        log.trace("Call /users POST request");
        return userService.updateUser(user);
    }

}
