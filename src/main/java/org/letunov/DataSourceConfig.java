package org.letunov;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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
    //@Value("${datasource.user}")
    private String user = "postgres";
    //@Value("${datasource.password}")
    private String password = "postgres";
    //@Value("${datasource.jdbcUrl}")
    private String jdbcUr = "jdbc:postgresql://postgres_db:5432/schedule";

    @Bean(destroyMethod = "close")
    public DataSource dataSource()
    {
//        try
//        {
            HikariConfig config = new HikariConfig();
            log.info("Configuration hikari pool with {} jdbcUrl", jdbcUr);
            config.setJdbcUrl(jdbcUr);
            log.info("Configuration hikari pool with {} username", user);
            config.setUsername(user);
            log.info("Configuration hikari pool with {} password", password);
            config.setPassword(password);
            config.setDriverClassName(org.postgresql.Driver.class.getName());
            return new HikariDataSource(config);
//        }
//        catch (Exception e)
//        {
//            log.error("Error hikari pool configuration: {}", e.getMessage());
//            return null;
//        }
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
