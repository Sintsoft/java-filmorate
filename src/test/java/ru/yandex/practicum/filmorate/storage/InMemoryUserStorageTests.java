package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class InMemoryUserStorageTests extends UserStorageTests {

    @BeforeEach
    void resetStorage() {
        testUserStorage = new InMemoryUserStorage();
    }
}
