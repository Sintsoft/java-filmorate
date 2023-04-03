package ru.yandex.practicum.filmorate.model;

import java.time.LocalDate;
import java.util.Set;
import java.util.TreeSet;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Past;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

@Data
@AllArgsConstructor
public class User {

    private int id;

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

    public void addFriend(User friend) {
        friends.add(friend.getId());
    }

    public void removeFriend(User friend) {
        friends.remove(friend.getId());
    }
}
