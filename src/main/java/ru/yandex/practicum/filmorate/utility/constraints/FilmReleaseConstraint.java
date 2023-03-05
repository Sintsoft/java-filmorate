package ru.yandex.practicum.filmorate.utility.constraints;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;

import javax.validation.Constraint;
import javax.validation.Payload;

import ru.yandex.practicum.filmorate.utility.validators.FilmReleaseConstraintValidator;

@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy=FilmReleaseConstraintValidator.class)
public @interface FilmReleaseConstraint {
    String message() default "Дата выпуска фильмне не можеть быть ранее 10 июня 1895";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
