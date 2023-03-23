package ru.yandex.practicum.filmorate.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.model.ErrorResponse;
import ru.yandex.practicum.filmorate.utility.exceptions.EntityValidationException;
import ru.yandex.practicum.filmorate.utility.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.utility.exceptions.UserNotFoundException;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {
    
    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
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

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleThrowable(final Throwable e) {
        log.error("Got unprdiacted throwable: " 
                   + e.getMessage()
                   + " " + e.getCause()
                   + " " + e.getStackTrace()
                   , e);
        return new ErrorResponse(
                "Произошла непредвиденная ошибка." 
        );
    }
}
