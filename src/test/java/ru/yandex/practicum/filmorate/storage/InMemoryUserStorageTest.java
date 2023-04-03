package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.BeforeEach;

public class InMemoryUserStorageTest extends UserStorageTest {

    @BeforeEach
    void resetStorage() {
        storage = new InMemoryUserStorage();
    }
}
