package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.model.Film;

import java.net.URI;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@WebMvcTest(FilmController.class)
public class FilmControllerTests {

    @MockBean
    FilmController filmController;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MockMvc mockMvc;

    @SneakyThrows
    @Test
    void nullFilmTest() {

        mockMvc.perform(post("/films", URI.create("/films"))
                    .contentType("application/json")
                    .content(objectMapper.writeValueAsString(null)))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void normalFilmTest() {
        Film film = new Film(0, "Test film", null, LocalDate.of(1995, 6, 1), 90, null);

        mockMvc.perform(post("/films", URI.create("/films"))
                    .contentType("application/json")
                    .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().isOk());
    }

    @SneakyThrows
    @Test
    void invalidDurationFilmTest() {
        Film film = new Film(0, "Test film", null, LocalDate.of(1995, 6, 1), -90, null);

        mockMvc.perform(post("/films", URI.create("/films"))
                    .contentType("application/json")
                    .content(objectMapper.writeValueAsString(film)));
        assertEquals(0, filmController.getAllFilms().size());
    }

}
