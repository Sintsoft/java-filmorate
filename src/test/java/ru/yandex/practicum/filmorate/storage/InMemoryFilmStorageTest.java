package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class InMemoryFilmStorageTest extends FilmStorageTest{

    @BeforeEach
    void resetStorage() {
        testFilmStorage = new InMemoryFilmStorage();
    }
}
