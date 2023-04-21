package ru.yandex.practicum.filmorate.utility.constraints;

import ru.yandex.practicum.filmorate.utility.validators.FilmReleaseConstraintValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy=FilmReleaseConstraintValidator.class)
public @interface FilmReleaseConstraint {
    String message() default "Relase date shoul be after 10.06.1895";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
