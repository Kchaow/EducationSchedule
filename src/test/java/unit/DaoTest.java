package unit;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.letunov.dao.AttendanceStatusDao;
import org.letunov.dao.GroupDao;
import org.letunov.dao.RoleDao;
import org.letunov.dao.UserDao;
import org.letunov.dao.impl.AttendanceStatusDaoImpl;
import org.letunov.dao.impl.GroupDaoImpl;
import org.letunov.dao.impl.RoleDaoImpl;
import org.letunov.dao.impl.UserDaoImpl;
import org.letunov.domainModel.AttendanceStatus;
import org.letunov.domainModel.Group;
import org.letunov.domainModel.Role;
import org.springframework.data.domain.Page;
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
public class DaoTest
{
    private AttendanceStatusDao attendanceStatusDao;
    private RoleDao roleDao;
    private UserDao userDao;
    private GroupDao groupDao;
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
        roleDao = new RoleDaoImpl(jdbcTemplate);
        userDao = new UserDaoImpl(jdbcTemplate);
        groupDao = new GroupDaoImpl(jdbcTemplate, userDao);
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

    @Test
    public void AttendanceStatusFindByNameTest()
    {
        AttendanceStatus attendanceStatus = attendanceStatusDao.findByName("присутствует");
        assertAll(
                () -> assertNotNull(attendanceStatus),
                () -> assertEquals(1, attendanceStatus.getId()),
                () -> assertEquals("присутствует", attendanceStatus.getName())
        );
    }

    @Test
    public void RoleFindAllTest()
    {
        List<Role> roleList = roleDao.findAll();
        assertEquals(3, roleList.size());
    }

    @Test
    public void RoleFindByIdTest()
    {
        Role role = roleDao.findById(1);
        assertAll(
                () -> assertNotNull(role),
                () -> assertEquals(1, role.getId()),
                () -> assertEquals("admin", role.getName())
        );
    }

    @Test
    public void GroupFindAllOrderByNameAscTest()
    {
        Page<Group> groupPage = groupDao.findAllOrderByNameAsc(10, 0);
        assertAll(
                () -> assertEquals(3, groupPage.getTotalElements()),
                () -> assertEquals(2, groupPage.getContent().getFirst().getUser().size()),
                () -> assertFalse(groupPage.getContent().getFirst().getUser().isEmpty())
        );
    }

    @Test
    public void GroupFindByIdTest()
    {
        Group group = groupDao.findById(1);
        assertAll(
                () -> assertNotNull(group),
                () -> assertEquals(1, group.getId()),
                () -> assertEquals("БСБО-01-21", group.getName())
        );
    }

    @Test
    public void GroupFindByNameTest()
    {
        Group group = groupDao.findByName("БСБО-01-21");
        assertAll(
                () -> assertNotNull(group),
                () -> assertEquals(1, group.getId()),
                () -> assertEquals("БСБО-01-21", group.getName())
        );
    }

    @Test
    public void GroupDeleteByIdTest()
    {
        groupDao.deleteById(1);
        assertNull(groupDao.findById(1));
    }
}
