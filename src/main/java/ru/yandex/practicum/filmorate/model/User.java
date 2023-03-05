package ru.yandex.practicum.filmorate.model;

import java.time.LocalDate;

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

    @NonNull
    @NotBlank(message = "Логин не должен быть пустым")
    private String login;

    @NonNull
    @Email(message = "Email должен быть валидным")
    private String email;

    @NonNull
    @Past(message =  "Дата рождения не может быть позже чем вчера")
    private LocalDate birthday;

    public void setName(String name) {
        if (name.isBlank()) {
            this.name = this.login;
        } else {
            this.name = name;
        }
    }

}
