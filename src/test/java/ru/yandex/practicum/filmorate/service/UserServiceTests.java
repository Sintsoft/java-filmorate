package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.utility.exceptions.EntityValidationException;
import ru.yandex.practicum.filmorate.utility.exceptions.IncorrectEntityIDException;
import ru.yandex.practicum.filmorate.utility.exceptions.UserNotFoundException;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@SqlGroup({

                @Sql(scripts = {"classpath:schema.sql"},
                        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
        }
)
public class UserServiceTests {

    @Autowired
    UserService testUserService;

    User getValidUserForTest() {
        return new User(
                0,
                "User Name",
                "Login",
                "email@yandex.ru",
                LocalDate.of(2000,1,1)
        );
    }

    @Test
    public void addVaildUserTest() {
        User testUser = getValidUserForTest();
        testUserService.addUser(testUser);
        assertEquals(1, testUser.getId());
    }

    @Test
    public void addInVaildUserTest() {
        User testUser = getValidUserForTest();
        testUser.setId(11);
        Exception e = assertThrows(IncorrectEntityIDException.class, () -> {
            testUserService.addUser(testUser);
        });
        assertEquals("Wrong method! User with id should be equal to 0", e.getMessage());
    }

    @Test
    public void addNullUserTest() {
        Exception e = assertThrows(EntityValidationException.class, () -> {
            testUserService.addUser(null);
        });
        assertEquals("Got null user", e.getMessage());
    }

    @Test
    public void updateVaildUserTest() {
        User testUser = getValidUserForTest();
        testUserService.addUser(testUser);
        assertEquals(1, testUser.getId());

        testUser.setName("New User Name");
        testUserService.updateUser(testUser);

        assertEquals("New User Name", testUserService.getUserById(1).getName());
    }

    @Test
    public void updateInVaildUserTest() {
        User testUser = getValidUserForTest();
        testUserService.addUser(testUser);
        assertEquals(1, testUser.getId());

        testUser.setName("New User Name");
        testUser.setId(2);
        assertThrows(UserNotFoundException.class, () -> {
            testUserService.updateUser(testUser);
        });
    }

    @Test
    public void deleteVaildUserTest() {
        User testUser = getValidUserForTest();
        testUserService.addUser(testUser);
        assertEquals(1, testUser.getId());

        testUserService.deleteUser(testUser);

        assertThrows(UserNotFoundException.class, () -> {
            testUserService.getUserById(1);
        });
    }

    @Test
    public void deleteInVaildUserTest() {
        User testUser = getValidUserForTest();
        testUserService.addUser(testUser);
        assertEquals(1, testUser.getId());

        testUser.setId(2);

        assertThrows(UserNotFoundException.class, () -> {
            testUserService.deleteUser(testUser);
        });
    }

    @Test
    public void setFriendsTest() {
        User testUser = getValidUserForTest();
        testUser.setLogin("User");
        testUserService.addUser(testUser);

        User testFriend = getValidUserForTest();
        testFriend.setLogin("Friend");
        testUserService.addUser(testFriend);

        testUserService.setFriend(1,2);
        assertEquals(1, testUserService.getUserById(1).getFriends().size());
        assertTrue(testUserService.getUserById(1).getFriends().contains(2));
    }
}
