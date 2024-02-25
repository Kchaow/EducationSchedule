package org.letunov;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import javax.xml.crypto.Data;

@Configuration
@PropertySource("classpath:database/datasource.properties")
@Log
public class DataSourceConfig
{
    @Value("${datasource.dataSourceClassName}")
    private String dataSourceClassName;
    @Value("${datasource.user}")
    private String user;
    @Value("${datasource.password}")
    private String password;
    @Value("${datasource.databaseName}")
    private String databaseName;
    @Value("${datasource.port}")
    private String port;
    @Value("${datasource.serverName}")
    private String serverName;

    @Bean(destroyMethod = "close")
    public DataSource dataSource()
    {
        try
        {
            HikariConfig config = new HikariConfig();
            config.setDataSourceClassName(dataSourceClassName);
            config.setUsername(user);
            config.setPassword(password);
            config.addDataSourceProperty("databaseName", databaseName);
            config.addDataSourceProperty("serverName", serverName);
            config.addDataSourceProperty("port", port);
            return new HikariDataSource(config);
        }
        catch (Exception e)
        {
            log.finest(e.getMessage());
            return null;
        }
    }

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource)
    {
        return new JdbcTemplate(dataSource);
    }
}
