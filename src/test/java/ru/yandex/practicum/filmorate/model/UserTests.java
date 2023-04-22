package ru.yandex.practicum.filmorate.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.utility.exceptions.IncorrectEntityIDException;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.Set;

import static javax.validation.Validation.buildDefaultValidatorFactory;
import static org.junit.jupiter.api.Assertions.*;

public class UserTests {

    // Создадим валидатор, для валидации полей
    Validator validator;

    @BeforeEach
    void setValidator() {
        ValidatorFactory factory = buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void allNullFieldsUserCreationTest() {
        assertThrows(NullPointerException.class, () -> new User(0, null, null, null, null));
    }

    @Test
    void nullNameUserCreationTest() {
        User testUser = new User(
                1,
                null,
                "Login",
                "email@yandex.ru",
                LocalDate.of(2000,1,1)
        );
        Set<ConstraintViolation<User>> violations = validator.validate(testUser);
        assertTrue(violations.isEmpty());
        assertNull(testUser.getName());
    }

    @Test
    void nullLoginUserCreationTest() {
        assertThrows(NullPointerException.class, () -> {
            new User(
                1,
                "User Name",
                null,
                "email@yandex.ru",
                LocalDate.of(2000,1,1)

            );
        });
    }

    @Test
    void futureBirthDateUserCreationTest() {
        User testUser = new User(
                1,
                "User Name",
                "Login",
                "email@yandex.ru",
                LocalDate.MAX
        );
        Set<ConstraintViolation<User>> violations = validator.validate(testUser);
        assertFalse(violations.isEmpty());
    }

    @Test
    void incorrectEmailUserCreationTest() {
        User testUser = new User(
                1,
                "User Name",
                "Login",
                "bla-bla-bal",
                LocalDate.of(2000,1,1)
        );
        Set<ConstraintViolation<User>> violations = validator.validate(testUser);
        assertFalse(violations.isEmpty());
    }

    @Test
    void addFriendTest() {
        User testUser = new User(
                1,
                "User Name",
                "Login",
                "email@yandex.ru",
                LocalDate.of(2000,1,1)
        );
        testUser.addFriend(2);
        assertEquals(1, testUser.getFriends().size());
    }

    @Test
    void addFriendWithIncorrectIdTest() {
        User testUser = new User(
                1,
                "User Name",
                "Login",
                "email@yandex.ru",
                LocalDate.of(2000,1,1)
        );
        assertThrows(IncorrectEntityIDException.class, () -> {
            testUser.addFriend(-2);
        });
    }

    @Test
    void removeFriendTest() {
        User testUser = new User(
                1,
                "User Name",
                "Login",
                "email@yandex.ru",
                LocalDate.of(2000,1,1)
        );
        testUser.addFriend(2);
        assertEquals(1, testUser.getFriends().size());
        testUser.removeFriend(2);
        assertEquals(0, testUser.getFriends().size());
    }

    @Test
    void removeFriendWithIncorrectIDTest() {
        User testUser = new User(
                1,
                "User Name",
                "Login",
                "email@yandex.ru",
                LocalDate.of(2000,1,1)
        );
        testUser.addFriend(2);
        assertEquals(1, testUser.getFriends().size());
        assertThrows(IncorrectEntityIDException.class, () -> {
            testUser.removeFriend(3);
        });
    }
}
