package ru.yandex.practicum.filmorate.utility.exceptions;

public class FilmNotFoundException  extends RuntimeException {

    public FilmNotFoundException(String message) {
        super(message);
    }
}
