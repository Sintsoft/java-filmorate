package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.model.Film;

import java.net.URI;
import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


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
        Film film = new Film(0, "Test film", null, LocalDate.of(1995, 6, 1), Duration.ofMinutes(90));

        mockMvc.perform(post("/films", URI.create("/films"))
                    .contentType("application/json")
                    .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().isOk());
    }

    @SneakyThrows
    @Test
    void invalidDurationFilmTest() {
        Film film = new Film(0, "Test film", null, LocalDate.of(1995, 6, 1),Duration.ofMinutes(-90));

        mockMvc.perform(post("/films", URI.create("/films"))
                    .contentType("application/json")
                    .content(objectMapper.writeValueAsString(film)));
        assertEquals(0, filmController.getAllFilms().size());
    }

    @SneakyThrows
    @Test
    void filmDurationResponseTest() {
        Film film = new Film(0, "Test film", null, LocalDate.of(1995, 6, 1),Duration.ofMinutes(90));

        mockMvc.perform(post("/films", URI.create("/films"))
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(film)));
        List<Film> retFilms = filmController.getAllFilms();
        Film respFilm = filmController.getFilm(1);
        assertEquals(1, retFilms.size());
    }
}
