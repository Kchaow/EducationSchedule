package unit;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.letunov.dao.AttendanceStatusDao;
import org.letunov.dao.impl.AttendanceStatusDaoImpl;
import org.letunov.domainModel.AttendanceStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.sql.Driver;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@Slf4j
public class AttendanceStatusDaoTest
{
    private AttendanceStatusDao attendanceStatusDao;
    private final String databaseName = "schedule";
    private final String password = "postgres";
    private final String username = "postgres";

    @SuppressWarnings("resource")
    @Container
    PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(DockerImageName.parse("postgres:latest"))
            .withDatabaseName(databaseName)
            .withPassword(password)
            .withUsername(username)
            .withInitScript("initialTestScript.sql");

    @BeforeEach
    public void setUpDataSource() throws ClassNotFoundException
    {
        SimpleDriverDataSource dataSource = new SimpleDriverDataSource();
        @SuppressWarnings("unchecked")
        Class<? extends Driver> driver = (Class<? extends Driver>) Class.forName(postgres.getDriverClassName());
        dataSource.setDriverClass(driver);
        dataSource.setUrl(postgres.getJdbcUrl());
        dataSource.setUsername(postgres.getUsername());
        dataSource.setPassword(postgres.getPassword());
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        attendanceStatusDao = new AttendanceStatusDaoImpl(jdbcTemplate);
    }

    @Test
    public void AttendanceStatusFindAllTest()
    {
        List<AttendanceStatus> attendanceStatusList = attendanceStatusDao.findAll();
        assertEquals(3, attendanceStatusList.size());
    }

    @Test
    public void AttendanceStatusFindByIdTest()
    {
        AttendanceStatus attendanceStatus = attendanceStatusDao.findById(1);
        assertAll(
                () -> assertNotNull(attendanceStatus),
                () -> assertEquals(1, attendanceStatus.getId()),
                () -> assertEquals("присутствует", attendanceStatus.getName())
        );
    }

}
