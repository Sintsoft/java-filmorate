package ru.yandex.practicum.filmorate.utility.databasework;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.yandex.practicum.filmorate.utility.DataBaseConnectionParams;
import ru.yandex.practicum.filmorate.utility.exceptions.DatabaseConnectionException;


import javax.sql.DataSource;


@Slf4j
@Configuration
public class DataBaseConnection {

    @Autowired
    DataBaseConnectionParams params;

    @Bean
    public DataSource getConnection() {
        try {
            DataSourceBuilder builder = DataSourceBuilder.create();
            builder.driverClassName(params.getDriverClassName());
            builder.url(params.getUrl());
            builder.username(params.getUserName());
            builder.password(params.getPassword());
            return builder.build();
        } catch (Exception e) {
            log.error("Failed create database connection to " + params.getUrl());
            throw new DatabaseConnectionException("Failde to create databse connection due to : "
                    + e.getClass() + " "
                    + e.getMessage());
        }
    }

}
