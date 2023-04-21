package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import ru.yandex.practicum.filmorate.utility.constraints.FilmReleaseConstraint;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

@Data
@Slf4j
@AllArgsConstructor
@RequiredArgsConstructor
public class Film {

    private int id = 0;

    @NonNull
    @NotBlank
    private String name;

    @Size(max = 200)
    private String description;

    @FilmReleaseConstraint
    private LocalDate releaseDate;

    // ЧЕРЕЗ DURATION НОРМАЛЬНО НЕ РАБОТАЕТ СДЕЛАНО ЧЕРЕЗ LONG!!!
    @Positive
    private long duration;

    private MPA mpa;

    private List<Genre> genres = new ArrayList<>();

    private final Set<Integer> likes = new TreeSet<>();

    public void likeFilm(int userId) {
        likes.add(userId);
    }

    public void dislikeFilm(int userId) {
        likes.remove(userId);
    }
}
