package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.DBGenreStorage;
import ru.yandex.practicum.filmorate.utility.exceptions.EntityNotFoundException;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class GenreService {

    @Autowired
    private final DBGenreStorage storage;

    public Genre getGenre(int id) {
        Optional<Genre> genre = storage.getGenre(id);
        if (genre.isEmpty()) {
            throw new EntityNotFoundException("Genre not found");
        }
        return genre.get();
    }

    public List<Genre> getAllGenres() {
        return storage.getAllGenres();
    }
}
