package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.utility.DataBaseConnectionParams;
import ru.yandex.practicum.filmorate.utility.exceptions.DatabaseConnectionEхception;
import ru.yandex.practicum.filmorate.utility.exceptions.EntityNotFoundException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@Slf4j
@Primary
public class MPAStorage {

    @Autowired
    DataBaseConnectionParams params;

    public Optional<MPA> getMPA(int id) {
        log.trace("Level: Storage. Method: getMPA. Input: " + id);
        try (
                Connection connection = DriverManager.getConnection(
                        params.getUrl(),
                        params.getUserName(),
                        params.getPassword()
                );
                PreparedStatement preparedStatement = connection.prepareStatement(
                        "SELECT * FROM MPA WHERE ID = ?",
                        Statement.RETURN_GENERATED_KEYS);
        ) {
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            Optional<MPA> returnMPA = Optional.empty();
            while (resultSet.next()) {
                 returnMPA = Optional.of(new MPA(resultSet.getInt(1), resultSet.getString(2)));
            }
            return returnMPA;
        } catch (SQLException e) {
            log.error("Failed getting film due to: " + e.getClass());
            throw new DatabaseConnectionEхception("Failed to get film");
        }
    }

    public List<MPA> getAllMPA() {
        log.trace("Level: Storage. Method: getMPA.");
        try (
                Connection connection = DriverManager.getConnection(
                        params.getUrl(),
                        params.getUserName(),
                        params.getPassword()
                );
                PreparedStatement preparedStatement = connection.prepareStatement(
                        "SELECT * FROM MPA",
                        Statement.RETURN_GENERATED_KEYS);
        ) {
            ResultSet resultSet = preparedStatement.executeQuery();
            List<MPA> returnMPA = new ArrayList<>();
            while (resultSet.next()) {
                returnMPA.add(
                    new MPA(
                        resultSet.getInt(1),
                        resultSet.getString(2)
                    )
                );
            }
            return returnMPA;
        } catch (SQLException e) {
            log.error("Failed getting film due to: " + e.getClass());
            throw new DatabaseConnectionEхception("Failed to get film");
        }
    }
}
