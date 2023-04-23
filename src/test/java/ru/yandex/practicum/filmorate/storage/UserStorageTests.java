package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.utility.exceptions.IncorrectEntityIDException;
import ru.yandex.practicum.filmorate.utility.exceptions.UserNotFoundException;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public abstract class UserStorageTests {

    UserStorage testUserStorage;

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
    void addVaildUserTest() {
        User testUser = getValidUserForTest();
        testUserStorage.addUser(testUser);
        testUser.setId(1);

        User userFromStorage = testUserStorage.getUser(1);

        assertEquals(1, testUserStorage.getAllUsers().size());
        assertEquals(testUser, userFromStorage);
    }

    @Test
    void addInVaildUserTest() {
        User testUser = getValidUserForTest();
        testUser.setId(-1);

        assertThrows(IncorrectEntityIDException.class, () ->  {
            testUserStorage.addUser(testUser);
        });
        assertEquals(0, testUserStorage.getAllUsers().size());

    }

    @Test
    void deleteVaildUserTest() {
        User testUser = getValidUserForTest();
        testUserStorage.addUser(testUser);
        testUser.setId(1);

        assertEquals(1, testUserStorage.getAllUsers().size());
        assertEquals(testUser, testUserStorage.getUser(1));

        testUserStorage.deleteUser(testUser);
        assertEquals(0, testUserStorage.getAllUsers().size());
    }

    @Test
    void deleteInvaildUserTest() {
        User testUser = getValidUserForTest();
        testUserStorage.addUser(testUser);
        testUser.setId(1);

        assertEquals(1, testUserStorage.getAllUsers().size());
        assertEquals(testUser, testUserStorage.getUser(1));

        User deleteUser = getValidUserForTest();
        deleteUser.setId(-1);
        assertThrows(UserNotFoundException.class, () -> {
            testUserStorage.deleteUser(deleteUser);
        });

        assertEquals(1, testUserStorage.getAllUsers().size());
    }

    @Test
    void updateValidUserTest() {
        User testUser = getValidUserForTest();
        testUserStorage.addUser(testUser);
        testUser.setId(1);

        assertEquals(1, testUserStorage.getAllUsers().size());
        assertEquals(testUser, testUserStorage.getUser(1));

        User newUpdateUser = new User(
                1,
                "New Name",
                "NewLogin",
                "newmail@yandex.ru",
                LocalDate.of(2002, 10, 10)
        );

        testUserStorage.updateUser(newUpdateUser);
        assertEquals(1, testUserStorage.getAllUsers().size());
        assertNotEquals(testUser, testUserStorage.getUser(1));
    }

    @Test
    void updateInvalidUserTest() {
        User testUser = getValidUserForTest();
        testUserStorage.addUser(testUser);
        testUser.setId(1);

        assertEquals(1, testUserStorage.getAllUsers().size());
        assertEquals(testUser, testUserStorage.getUser(1));

        User newUpdateUser = new User(
                -1,
                "New Name",
                "NewLogin",
                "newmail@yandex.ru",
                LocalDate.of(2002, 10, 10)
        );

        assertThrows(UserNotFoundException.class, () -> {
            testUserStorage.updateUser(newUpdateUser);
        });
    }

    @Test
    void saveFriendShipWithVaildUsersTest() {
        User testUser = getValidUserForTest();
        testUser.setLogin("User");
        testUserStorage.addUser(testUser);

        User testFriend = getValidUserForTest();
        testFriend.setLogin("Friend");
        testUserStorage.addUser(testFriend);

        assertEquals(2, testUserStorage.getAllUsers().size());

        testUserStorage.saveFriendShip(1, 2);
        assertEquals(1, testUserStorage.getUser(1).getFriends().size());
        assertTrue(testUserStorage.getUser(1).getFriends().contains(2));
        assertEquals(0, testUserStorage.getUser(2).getFriends().size());
        assertFalse(testUserStorage.getUser(2).getFriends().contains(1));
    }

    @Test
    void saveFriendShipWithInVaildUserTest() {

        User testFriend = getValidUserForTest();
        testFriend.setLogin("Friend");
        testUserStorage.addUser(testFriend);

        assertThrows(Exception.class, () -> {
            testUserStorage.saveFriendShip(1, 2);
        });
    }

    @Test
    void saveFriendShipWithInVaildFriendTest() {

        User testFriend = getValidUserForTest();
        testFriend.setLogin("Friend");
        testUserStorage.addUser(testFriend);

        assertThrows(Exception.class, () -> {
            testUserStorage.saveFriendShip(2, 1);
        });
    }

    @Test
    void eraseFriendShipWithVaildUsersTest() {
        User testUser = getValidUserForTest();
        testUser.setLogin("User");
        testUserStorage.addUser(testUser);

        User testFriend = getValidUserForTest();
        testFriend.setLogin("Friend");
        testUserStorage.addUser(testFriend);

        assertEquals(2, testUserStorage.getAllUsers().size());

        testUserStorage.saveFriendShip(1, 2);
        assertEquals(1, testUserStorage.getUser(1).getFriends().size());
        assertTrue(testUserStorage.getUser(1).getFriends().contains(2));
        assertEquals(0, testUserStorage.getUser(2).getFriends().size());
        assertFalse(testUserStorage.getUser(2).getFriends().contains(1));
        assertEquals(2, testUserStorage.getAllUsers().size());

        testUserStorage.eraseFrienShip(1, 2);
        assertEquals(0, testUserStorage.getUser(1).getFriends().size());
        assertFalse(testUserStorage.getUser(1).getFriends().contains(2));
        assertEquals(0, testUserStorage.getUser(2).getFriends().size());
        assertFalse(testUserStorage.getUser(2).getFriends().contains(1));
    }

    @Test
    void eraseFriendShipWithInvaildUsersTest() {
        User testUser = getValidUserForTest();
        testUser.setLogin("User");
        testUserStorage.addUser(testUser);

        User testFriend = getValidUserForTest();
        testFriend.setLogin("Friend");
        testUserStorage.addUser(testFriend);

        assertEquals(2, testUserStorage.getAllUsers().size());

        testUserStorage.saveFriendShip(1, 2);
        assertEquals(1, testUserStorage.getUser(1).getFriends().size());
        assertTrue(testUserStorage.getUser(1).getFriends().contains(2));
        assertEquals(0, testUserStorage.getUser(2).getFriends().size());
        assertFalse(testUserStorage.getUser(2).getFriends().contains(1));

        assertThrows(Exception.class, () -> {
            testUserStorage.eraseFrienShip(1, 3);
        });
        assertEquals(1, testUserStorage.getUser(1).getFriends().size());
        assertTrue(testUserStorage.getUser(1).getFriends().contains(2));
        assertEquals(0, testUserStorage.getUser(2).getFriends().size());
        assertFalse(testUserStorage.getUser(2).getFriends().contains(1));
    }
}
