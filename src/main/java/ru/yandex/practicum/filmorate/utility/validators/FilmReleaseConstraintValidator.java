package ru.yandex.practicum.filmorate.utility.validators;

import java.time.LocalDate;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import ru.yandex.practicum.filmorate.utility.constraints.FilmReleaseConstraint;

public class FilmReleaseConstraintValidator implements ConstraintValidator<FilmReleaseConstraint, LocalDate> {
    
    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        return value.isAfter(LocalDate.of(1895, 6, 10));
    }
}
