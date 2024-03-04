package unit;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.letunov.dao.*;
import org.letunov.dao.impl.*;
import org.letunov.domainModel.*;
import org.letunov.exceptions.TheDependentEntityIsPreservedBeforeTheIndependentEntity;
import org.springframework.data.domain.Page;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.sql.Driver;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
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
    private SubjectDao subjectDao;
    private EducationDayDao educationDayDao;
    private AttendanceDao attendanceDao;
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
    public void setUpDataSource() throws ClassNotFoundException {
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
        subjectDao = new SubjectDaoImpl(jdbcTemplate);
        educationDayDao = new EducationDayDaoImpl(jdbcTemplate, userDao, subjectDao, groupDao);
        attendanceDao = new AttendanceDaoImpl(jdbcTemplate, educationDayDao, userDao, attendanceStatusDao);
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
        newUser.setEmail("mailer");
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
    public void UserFindByRoleTest()
    {
        assertEquals(6, userDao.findByRole("student", 10, 0).getContent().size());
    }

    @Test
    public void UserSaveAllTest()
    {
        int size = 10;
        List<User> users = new ArrayList<>();
        Group group = groupDao.findById(2);
        Role role = roleDao.findById(2);
        for (int i = 0; i < size; i++)
        {
            User user = new User();
            user.setFirstName("firstName");
            user.setLastName("lastName");
            user.setEmail("mail %d".formatted(i));
            user.setLogin("login %d".formatted(i));
            user.setPassword("1234");
            user.setRole(role);
            user.setGroup(group);
            users.add(user);
        }
        users.get(5).setFirstName("newName");

        userDao.saveAll(users);
        assertAll(
                () -> assertEquals(16, userDao.findByRole("student", 200, 0).getTotalElements()),
                () -> assertNotNull(userDao.findByFirstNameOrderByFirstName("newName", 10, 0))
        );
    }

    @Test
    public void SubjectFindByIdTest()
    {
        assertEquals("Методы обеспечения целостности информации", subjectDao.findById(2).getName());
    }

    @Test
    public void SubjectFindByNameTest()
    {
        assertNotNull(subjectDao.findByName("Методы обеспечения целостности информации"));
    }

    @Test
    public void SubjectSaveTest()
    {
        Subject subject = new Subject();
        String name = "New subject";
        subject.setName(name);
        subjectDao.save(subject);
        assertNotNull(subjectDao.findByName(name));
    }

    @Test
    public void SubjectDeleteById()
    {
        subjectDao.deleteById(1);
        assertNull(subjectDao.findById(1));
    }

    @Test
    public void EducationDayFindByWeekNumberOrderByDateAscClassNumberAsc()
    {
        List<EducationDay> educationDayList = educationDayDao.findByWeekNumberOrderByDateAscClassNumberAsc(1);
        assertAll(
                () -> assertEquals(6, educationDayList.size()),
                () -> assertEquals(2, educationDayList.getFirst().getGroup().size()),
                () -> assertEquals("Крылов", educationDayList.getFirst().getUser().getFirstName())
        );
    }

    @Test
    public void EducationDayFindByWeekNumberAndTeacherOrderByDateAscClassNumberAsc()
    {
        User user = userDao.findById(2);
        List<EducationDay> educationDayList = educationDayDao.findByWeekNumberAndTeacherOrderByDateAscClassNumberAsc(1, user);
        assertAll(
                () -> assertEquals(4, educationDayList.size()),
                () -> assertEquals(2, educationDayList.getFirst().getGroup().size()),
                () -> assertEquals("Крылов", educationDayList.getFirst().getUser().getFirstName())
        );
    }

    @Test
    public void EducationDayFindByWeekNumberAndGroupOrderByDateAscClassNumberAsc()
    {
        Group group = groupDao.findById(1);
        List<EducationDay> educationDayList = educationDayDao.findByWeekNumberAndGroupOrderByDateAscClassNumberAsc(1, group);
        assertAll(
                () -> assertEquals(2, educationDayList.size()),
                () -> assertEquals(2, educationDayList.getFirst().getGroup().size()),
                () -> assertEquals("Крылов", educationDayList.getFirst().getUser().getFirstName())
        );
    }

    @Test
    public void EducationDayFindById()
    {
        EducationDay educationDay = educationDayDao.findById(1);
        assertAll(
                () -> assertNotNull(educationDay),
                () -> assertEquals(2, educationDay.getGroup().size()),
                () -> assertEquals("Крылов", educationDay.getUser().getFirstName())
        );
    }

    @Test
    public void EducationDayDeleteById()
    {
        long educationDayId = 1;
        educationDayDao.deleteById(educationDayId);
        assertNull(educationDayDao.findById(educationDayId));
    }

    @Test
    public void EducationDaySave()
    {
        List<Group> groupList = groupDao.findAllOrderByNameAsc(10, 0).getContent();
        User user = userDao.findById(1);
        Subject subject = subjectDao.findById(1);
        EducationDay educationDay = new EducationDay();
        educationDay.setWeekNumber(1);
        educationDay.setClassNumber(3);
        educationDay.setDate(LocalDate.now());
        educationDay.setAudience(256);
        educationDay.setUser(user);
        educationDay.setSubject(subject);
        educationDay.setGroup(groupList);
        educationDayDao.save(educationDay);

        long id = 1;
        EducationDay oldEducationDay = educationDayDao.findById(id);
        oldEducationDay.getGroup().removeFirst();
        oldEducationDay.setAudience(234);
        educationDayDao.save(oldEducationDay);


        assertAll(
                () -> assertEquals(7, educationDayDao.findByWeekNumberOrderByDateAscClassNumberAsc(1).size()),
                () -> assertEquals(1, educationDayDao.findById(id).getGroup().size())
        );
    }

    @Test
    public void AttendanceFindByStudentIdAndEducationDayId()
    {
        assertEquals(1, attendanceDao.findByStudentIdAndEducationDayId(4, 2).size());
    }

    @Test
    public void AttendanceFindByEducationDayDateAndEducationDaySubject()
    {
        LocalDate localDate = LocalDate.of(2024, 2, 19);
        Subject subject = subjectDao.findById(1);
        Attendance attendance = attendanceDao.findByEducationDayDateAndEducationDaySubject(localDate, subject);
        assertAll(
                () -> assertNotNull(attendance),
                () -> assertEquals(2, attendance.getEducationDay().getGroup().size())
        );
    }

    @Test
    public void AttendanceFindById()
    {
        Attendance attendance = attendanceDao.findById(1);
        assertNotNull(attendance);
    }

    @Test
    public void AttendanceDeleteById()
    {
        attendanceDao.deleteById(1);
        assertNull(attendanceDao.findById(1));
    }

    @Test
    public void AttendanceSave()
    {
        attendanceDao.deleteById(1);
        long userId = 4;
        User user = userDao.findById(userId);
        AttendanceStatus attendanceStatus = attendanceStatusDao.findById(1);
        EducationDay educationDay = educationDayDao.findById(1);
        Attendance attendance = new Attendance();
        attendance.setAttendanceStatus(attendanceStatus);
        attendance.setUser(user);
        attendance.setEducationDay(educationDay);

        AttendanceStatus attendanceStatus1 = attendanceStatusDao.findById(3);
        Attendance oldAttendance = attendanceDao.findById(2);
        oldAttendance.setAttendanceStatus(attendanceStatus1);

        assertAll(
                () -> assertEquals(4, attendanceDao.save(attendance).getUser().getId()),
                () -> assertEquals(3, attendanceDao.save(oldAttendance).getAttendanceStatus().getId())
        );
    }
}
