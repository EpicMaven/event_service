package org.novalabs.event.configuration;

import javax.sql.DataSource;
import org.novalabs.event.dao.sql.EventSqlDao;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring Boot database configuration file.
 */
@Configuration
public class SqlConfig {

    @Bean("eventH2Dao")
    public EventSqlDao eventH2Dao(DataSource dataSource) {
        return new EventSqlDao(dataSource);
    }
}
