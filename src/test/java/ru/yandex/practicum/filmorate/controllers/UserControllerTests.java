package ru.yandex.practicum.filmorate.controllers;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.SneakyThrows;

import java.net.URI;
import java.time.LocalDate;

import ru.yandex.practicum.filmorate.model.User;

@WebMvcTest
public class UserControllerTests {
    
    @MockBean
    private UserController userController;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @SneakyThrows
    @Test
    void nullUserCreationTest() {
        mockMvc.perform(post("/users", URI.create("/films"))
                    .contentType("application/json")
                    .content(objectMapper.writeValueAsString(null)))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void normalUserCreationTest() {

        User user = new User(0, "Name", "login", "mail@mail.ru", LocalDate.of(1999, 1, 1));

        mockMvc.perform(post("/users", URI.create("/films"))
                    .contentType("application/json")
                    .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk());
    }

    @SneakyThrows
    @Test
    void noNameUserCreationTest() {

        User user = new User(0, null, "login", "mail@mail.ru", LocalDate.of(1999, 1, 1));

        mockMvc.perform(post("/users", URI.create("/films"))
                    .contentType("application/json")
                    .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk());
    }

    @SneakyThrows
    @Test
    void notEmailUserCreationTest() {

        User user = new User(0, null, "login", "mail", LocalDate.of(1999, 1, 1));

        mockMvc.perform(post("/users", URI.create("/films"))
                    .contentType("application/json")
                    .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest());
    }

}