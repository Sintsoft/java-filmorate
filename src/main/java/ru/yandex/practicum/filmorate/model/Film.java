package ru.yandex.practicum.filmorate.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Set;
import java.util.TreeSet;
import java.time.Duration;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import org.springframework.boot.convert.DurationUnit;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.utility.constraints.FilmReleaseConstraint;

@Data
@Slf4j
@AllArgsConstructor
public class Film {

    private int id;

    private final Set<Integer> likes = new TreeSet<>();

    @DurationUnit(ChronoUnit.HOURS)
    private Duration duration;

    @NonNull
    @NotBlank
    private String name;

    @Size(max = 200)
    private String description;

    @FilmReleaseConstraint
    private LocalDate releaseDate;

    public long getDuration() {
        log.trace("Film {} duartion {} in minutes {}", this.id, this.duration, this.duration.toSeconds());
        return this.duration.toSeconds();
    }

    public void likeFilm(int userId) {
        likes.add(userId);
    }

    public void dislikeFilm(int userId) {
        likes.remove(userId);
    }

}
