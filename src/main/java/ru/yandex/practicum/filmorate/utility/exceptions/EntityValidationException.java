package ru.yandex.practicum.filmorate.utility.exceptions;

public class EntityValidationException extends RuntimeException {

    public EntityValidationException() {
    }

    public EntityValidationException(String message) {
        super(message);
    }
}
