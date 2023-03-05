package ru.yandex.practicum.filmorate.model;

import java.time.LocalDate;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import ru.yandex.practicum.filmorate.utility.constraints.FilmReleaseConstraint;

@Data
@AllArgsConstructor
public class Film {
    
    private int id;

    @Min(1)
    private int duration;

    @NonNull
    @NotBlank
    private String name;

    @Size(max = 200)
    private String description;

    @FilmReleaseConstraint
    private LocalDate releaseDate;    
}
