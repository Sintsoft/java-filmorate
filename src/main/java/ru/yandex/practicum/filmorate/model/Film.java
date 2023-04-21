package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.convert.DurationUnit;
import org.springframework.lang.NonNull;
import ru.yandex.practicum.filmorate.utility.constraints.FilmReleaseConstraint;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.Duration;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
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

    @DurationUnit(ChronoUnit.HOURS)
    private Duration duration;

    private MPA mpa;

    private final Set<Integer> likes = new TreeSet<>();

    public long getDuration() {
        log.debug("Film {} duartion {} in minutes {}", this.id, this.duration, this.duration.toSeconds());
        return this.duration.toSeconds();
    }

    public void likeFilm(int userId) {
        likes.add(userId);
    }

    public void dislikeFilm(int userId) {
        likes.remove(userId);
    }
}
