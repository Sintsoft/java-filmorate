package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.MPA;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Component
@Slf4j
@Primary
@RequiredArgsConstructor
public class MPAStorage {

    private static final String GET_MPA_QUERY = "SELECT * FROM MPA WHERE ID = ?";
    private static final String GET_ALL_MPA_QUERY = "SELECT * FROM MPA";

    @Autowired
    private final JdbcTemplate jdbc;

    public Optional<MPA> getMPA(int id) {
        log.trace("Level: Storage. Method: getMPA. Input: " + id);
        Optional<MPA> mpa = Optional.empty();
        try {
            MPA obj = jdbc.queryForObject(
                    GET_MPA_QUERY,
                    new Object[]{id},
                    new MPARowMapper()
            );
            if (obj != null) {
                mpa = Optional.of(obj);
            }
        } catch (EmptyResultDataAccessException e) {
            log.info("MPA with id = " + id + " not exist");;
        }
        return mpa;
    }

    public List<MPA> getAllMPA() {
        log.trace("Level: Storage. Method: getAllMPA.");
        return jdbc.query(GET_ALL_MPA_QUERY, new MPARowMapper());
    }

    private static class MPARowMapper implements RowMapper<MPA> {

        @Override
        public MPA mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new MPA(
                    rs.getInt(1),
                    rs.getString(2)
            );
        }
    }
}
