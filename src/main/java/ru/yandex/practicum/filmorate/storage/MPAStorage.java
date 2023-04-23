package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.utility.databasework.DataBaseConnectionParams;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@Slf4j
@Primary
public class MPAStorage {

    private final String GET_MPA_QUERY = "SELECT * FROM MPA WHERE ID = ?";
    private final String GET_ALL_MPA_QUERY = "SELECT * FROM MP";

    @Autowired
    DataBaseConnectionParams params;

    @Autowired
    JdbcTemplate jdbc;

    public Optional<MPA> getMPA(int id) {
        log.trace("Level: Storage. Method: getMPA. Input: " + id);
        return Optional.ofNullable(
            MPA.class.cast(
                jdbc.queryForObject(
                    GET_MPA_QUERY,
                    new Object[]{id},
                    new MPARowMapper()
                )
            )
        );
    }

    public List<MPA> getAllMPA() {
        log.trace("Level: Storage. Method: getAllMPA.");
        List<MPA> returnMPA = new ArrayList<>();
        SqlRowSet resultSet = jdbc.queryForRowSet(GET_ALL_MPA_QUERY);
        while (resultSet.next()) {
            returnMPA.add(
                    new MPA(
                            resultSet.getInt(1),
                            resultSet.getString(2)
                    )
            );
        }
        return returnMPA;
    }

    private static class MPARowMapper implements RowMapper {

        @Override
        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new MPA(
                    rs.getInt(1),
                    rs.getString(2)
            );
        }
    }
}
