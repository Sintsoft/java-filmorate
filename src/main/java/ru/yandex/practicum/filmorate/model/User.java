package ru.yandex.practicum.filmorate.model;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.utility.exceptions.IncorrectEntityIDException;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Past;
import java.time.LocalDate;
import java.util.Set;
import java.util.TreeSet;

@Slf4j
@Data
@AllArgsConstructor
public class User {

    private int id = 0;
    private String name;
    private final Set<Integer> friends = new TreeSet<>();

    @NonNull
    @NotBlank(message = "Логин не должен быть пустым")
    private String login;

    @NonNull
    @Email(message = "Email должен быть валидным")
    private String email;

    @NonNull
    @Past(message =  "Дата рождения не может быть позже чем вчера")
    private LocalDate birthday;

    public void addFriend(int friendId) {
        log.debug("Attempting to add friend to user " + this.id + " with friend id " + friendId);
        if (friendId > 0) {
            friends.add(friendId);
            log.trace("Succesfully added friend with id " + friendId + " to user " + this.id);
        } else {
            log.trace("Got incorrect friend id - " + friendId + ". Throwing exception");
            throw new IncorrectEntityIDException("Incorrect friend id");
        }
    }

    public void removeFriend(int friendId) {
        log.debug("Attempting to remove friend from user " + this.id + " with friend id " + friendId);
        if (friendId <= 0 || !friends.contains(friendId)) {
            log.trace("Got incorrect friend id - " + friendId + ". Throwing exception");
            throw new IncorrectEntityIDException("Incorrect friend id");
        } else {
            friends.remove(friendId);
            log.trace("Succesfully removed friend with id " + friendId + " from user " + this.id);
        }
    }
}
