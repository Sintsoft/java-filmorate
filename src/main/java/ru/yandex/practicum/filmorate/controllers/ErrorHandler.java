package ru.yandex.practicum.filmorate.controllers;

import javax.validation.ValidationException;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.model.ErrorResponse;
import ru.yandex.practicum.filmorate.utility.exceptions.EntityValidationException;
import ru.yandex.practicum.filmorate.utility.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.utility.exceptions.NullPayloadObjectException;
import ru.yandex.practicum.filmorate.utility.exceptions.UserNotFoundException;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleNullPayloadObjectException(final NullPayloadObjectException e) {
        log.info("Nothing found in payload");
        return new ErrorResponse("Nothing found in payload");
    }
    
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleSpringValidationError(final ValidationException e) {
        return new ErrorResponse("Entity validation exception. " + e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleInvaildEntity(final EntityValidationException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleUserNotFound(final UserNotFoundException e) {
        return new ErrorResponse("User Not Found");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleFilmNotFound(final FilmNotFoundException e) {
        return new ErrorResponse("User Not Found");
    }

}
