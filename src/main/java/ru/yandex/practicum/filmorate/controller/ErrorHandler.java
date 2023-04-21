package ru.yandex.practicum.filmorate.controller;



import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import ru.yandex.practicum.filmorate.model.ErrorResponse;
import ru.yandex.practicum.filmorate.utility.exceptions.*;

import java.sql.SQLException;

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
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleUserNotFound(final UserNotFoundException e) {
        return new ErrorResponse("User Not Found");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleSQLException(final SQLException e) {
        return new ErrorResponse("Internal DB SQL execution exception");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleDatabaseConnectionEхception(final DatabaseConnectionEхception e) {
        return new ErrorResponse("Internal DB exception");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleIncorrectEntityIDException(IncorrectEntityIDException e) {
        return new ErrorResponse("Use correct id in your request");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleEntityValidationException(EntityValidationException e) {
        return new ErrorResponse("Use correct entity in your request");
    }
}