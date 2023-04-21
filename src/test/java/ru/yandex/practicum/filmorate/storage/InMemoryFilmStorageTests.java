package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class InMemoryFilmStorageTests extends FilmStorageTests {

    @BeforeEach
    void resetStorage() {
        testFilmStorage = new InMemoryFilmStorage();
    }
}
