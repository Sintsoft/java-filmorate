package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;

@Slf4j
@SpringBootTest
@SqlGroup({
                @Sql(scripts = {"classpath:schema.sql"},
                        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
        }
)
@EnableConfigurationProperties
public class DBFilmStorageTests extends FilmStorageTest {

    @Autowired
    DbFilmStorage testDbStorage;

    @Autowired
    DbUserStorage testDbUserStorage;

    @BeforeEach
    void setDB() {
        log.info("Resetting user storage");
        this.testFilmStorage = testDbStorage;
        this.testUserStorage = testDbUserStorage;
    }
}

