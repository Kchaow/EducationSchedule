package org.letunov;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.JdbcTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
@PropertySource("classpath:database/datasource.properties")
@Slf4j
public class DataSourceConfig
{
    @Bean(destroyMethod = "close")
    public DataSource dataSource()
    {
        HikariConfig config = new HikariConfig();
        String jdbcUrl = "jdbc:postgresql://postgres_db:5432/schedule";
        log.info("Configuration hikari pool with {} jdbcUrl", jdbcUrl);
        config.setJdbcUrl(jdbcUrl);
        String user = "postgres";
        log.info("Configuration hikari pool with {} username", user);
        config.setUsername(user);
        String password = "postgres";
        log.info("Configuration hikari pool with {} password", password);
        config.setPassword(password);
        config.setDriverClassName(org.postgresql.Driver.class.getName());
        return new HikariDataSource(config);
    }

    @Bean
    public PlatformTransactionManager platformTransactionManager(DataSource dataSource)
    {
        log.info("Configuration PlatformTransactionManager with dataSource");
        return new JdbcTransactionManager(dataSource);
    }

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource)
    {
        log.info("Configuration JdbcTemplate");
        return new JdbcTemplate(dataSource);
    }
}
