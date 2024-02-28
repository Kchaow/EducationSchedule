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
import org.letunov.domainModel.User;
import org.letunov.exceptions.TheDependentEntityIsPreservedBeforeTheIndependentEntity;
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
        groupDao = new GroupDaoImpl(jdbcTemplate);
        userDao = new UserDaoImpl(jdbcTemplate, groupDao, roleDao);
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
        assertEquals(3, groupPage.getTotalElements());
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

    @Test
    public void GroupSaveTest()
    {
        long id = 1;
        Group group = groupDao.findById(id);
        group.setName("old group");
        groupDao.save(group);

        Group newGroup = new Group();
        newGroup.setName("new group");
        groupDao.save(newGroup);

        assertAll(
                () -> assertEquals("old group", groupDao.findById(id).getName()),
                () -> assertEquals("new group", groupDao.findById(4).getName())
        );

    }

    @Test
    public void UserFindByIdTest()
    {
        User user = userDao.findById(4);
        assertAll(
                () -> assertNotNull(user),
                () -> assertEquals(4, user.getId()),
                () -> assertEquals("Копылова", user.getFirstName())
        );
    }

    @Test
    public void UserSaveTest()
    {
        long id = 4;
        User oldUser = userDao.findById(id);
        oldUser.setFirstName("Lora");
        userDao.save(oldUser);

        User newUser = new User();
        newUser.setFirstName("Ryan");
        newUser.setLastName("Gosling");
        newUser.setLogin("gos");
        newUser.setPassword("1234");
        Role role = roleDao.findAll().getFirst();
        newUser.setRole(role);
        Group newGroup = new Group();
        newGroup.setName("new group");
        newUser.setGroup(newGroup);

        User newUser2 = new User();
        newUser2.setFirstName("Ryan");
        newUser2.setLastName("Gosling");
        newUser2.setLogin("gos");
        newUser2.setEmail("mail");
        newUser2.setPassword("1234");
        role = roleDao.findAll().get(1);
        newUser2.setRole(role);
        newGroup = groupDao.findById(2);
        newGroup.setName("new group");
        newUser2.setGroup(newGroup);

        assertAll(
                () -> assertEquals("Lora", userDao.findById(id).getFirstName()),
                () -> assertThrows(TheDependentEntityIsPreservedBeforeTheIndependentEntity.class, () -> userDao.save(newUser)),
                () -> assertEquals("Ryan", userDao.save(newUser2).getFirstName())
        );
    }

    @Test
    public void UserFindByFirstNameOrderByFirstName()
    {
        assertEquals(1, userDao.findByFirstNameOrderByFirstName("Тихонова", 10, 0).getContent().getFirst().getId());
    }

    @Test
    public void UserFindByLastNameOrderByLastName()
    {
        assertEquals(3, userDao.findByLastNameOrderByLastName("Лука", 10, 0).getContent().getFirst().getId());
    }

    @Test
    public void UserFindByMiddleNameOrderByMiddleName()
    {
        assertEquals(6, userDao.findByMiddleNameOrderByMiddleName("Игоревна", 10, 0).getContent().getFirst().getId());
    }

    @Test
    public void UserFindByLoginTest()
    {
        User user = userDao.findByLogin("kol_var");
        assertAll(
                () -> assertNotNull(user),
                () -> assertEquals(6, user.getId()),
                () -> assertEquals("kol_var", user.getLogin())
        );
    }

    @Test
    public void UserFindByEmailTest()
    {
        User user = userDao.findByEmail("kol_var@gmail.com");
        assertAll(
                () -> assertNotNull(user),
                () -> assertEquals(6, user.getId()),
                () -> assertEquals("kol_var@gmail.com", user.getEmail())
        );
    }

    @Test
    public void UserDeleteByIdTest()
    {
        userDao.deleteById(6);
        assertNull(groupDao.findById(6));
    }

    @Test
    public void UserFindByRole()
    {
        assertEquals(6, userDao.findByRole("student", 10, 0).getContent().size());
    }
}