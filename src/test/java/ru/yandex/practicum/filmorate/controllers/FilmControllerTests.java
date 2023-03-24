package ru.yandex.practicum.filmorate.controllers;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.SneakyThrows;

import java.net.URI;
import java.time.Duration;
import java.time.LocalDate;

import ru.yandex.practicum.filmorate.model.Film;

@WebMvcTest(FilmController.class)
public class FilmControllerTests {
    
    @MockBean
    private FilmController filmController;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

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
        Film film = new Film(0, Duration.ofMinutes(90), "Test film", null, LocalDate.of(1995, 6, 1));

        mockMvc.perform(post("/films", URI.create("/films"))
                    .contentType("application/json")
                    .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().isOk());
    }

    @SneakyThrows
    @Test
    void invalidDurationFilmTest() {
        Film film = new Film(0, Duration.ofSeconds(-90), "Test film", null, LocalDate.of(1995, 6, 1));

        mockMvc.perform(post("/films", URI.create("/films"))
                    .contentType("application/json")
                    .content(objectMapper.writeValueAsString(film)));
        assertEquals(0, filmController.getAllFilms().size());
    }
}
