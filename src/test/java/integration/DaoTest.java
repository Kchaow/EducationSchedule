package integration;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.letunov.dao.*;
import org.letunov.dao.ClassDao;
import org.letunov.dao.impl.*;
import org.letunov.domainModel.*;
import org.letunov.domainModel.Class;
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
//@Disabled
public class DaoTest
{
    private AttendanceStatusDao attendanceStatusDao;
    private RoleDao roleDao;
    private UserDao userDao;
    private GroupDao groupDao;
    private SubjectDao subjectDao;
    private ClassDao aClassDao;
    private ScheduleTemplateDao scheduleTemplateDao;
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
        java.lang.Class<? extends Driver> driver = (java.lang.Class<? extends Driver>) java.lang.Class.forName(postgres.getDriverClassName());
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
        scheduleTemplateDao = new ScheduleTemplateDaoImpl(jdbcTemplate);
        aClassDao = new ClassDaoImpl(jdbcTemplate, userDao, subjectDao, groupDao, scheduleTemplateDao);
        attendanceDao = new AttendanceDaoImpl(jdbcTemplate, aClassDao, userDao, attendanceStatusDao);
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
    public void ClassFindByWeekNumberOrderByDateAscClassNumberAsc()
    {
        List<Class> classList = aClassDao.findByWeekNumberOrderByDayOfWeekAscClassNumberAsc(1);
        assertAll(
                () -> assertEquals(6, classList.size()),
                () -> assertEquals(2, classList.getFirst().getGroup().size()),
                () -> assertEquals("Крылов", classList.getFirst().getUser().getFirstName())
        );
    }

    @Test
    public void ClassFindByWeekNumberOrderByDateAscClassNumberAscScheduleTemplate()
    {
        ScheduleTemplate scheduleTemplate = new ScheduleTemplate();
        scheduleTemplate.setId(1);
        List<Class> classList = aClassDao.findByWeekNumberOrderByDayOfWeekAscClassNumberAsc(1, scheduleTemplate);

        assertAll(
                () -> assertEquals(6, classList.size()),
                () -> assertEquals(2, classList.getFirst().getGroup().size()),
                () -> assertEquals("Крылов", classList.getFirst().getUser().getFirstName())
        );
    }

    @Test
    public void ClassFindByWeekNumberAndTeacherOrderByDateAscClassNumberAsc()
    {
        User user = userDao.findById(2);
        List<Class> classList = aClassDao.findByWeekNumberAndTeacherOrderByDayOfWeekAscClassNumberAsc(1, user);
        assertAll(
                () -> assertEquals(4, classList.size()),
                () -> assertEquals(2, classList.getFirst().getGroup().size()),
                () -> assertEquals("Крылов", classList.getFirst().getUser().getFirstName())
        );
    }

    @Test
    public void ClassFindByWeekNumberAndTeacherOrderByDayOfWeekAscClassNumberAscScheduleTemplate()
    {
        User user = userDao.findById(2);
        ScheduleTemplate scheduleTemplate = new ScheduleTemplate();
        scheduleTemplate.setId(1);
        List<Class> classList = aClassDao.findByWeekNumberAndTeacherOrderByDayOfWeekAscClassNumberAsc(1, user, scheduleTemplate);
        assertAll(
                () -> assertEquals(4, classList.size()),
                () -> assertEquals(2, classList.getFirst().getGroup().size()),
                () -> assertEquals("Крылов", classList.getFirst().getUser().getFirstName())
        );
    }

    @Test
    public void ClassFindByWeekNumberAndGroupOrderByDateAscClassNumberAsc()
    {
        Group group = groupDao.findById(1);
        List<Class> classList = aClassDao.findByWeekNumberAndGroupOrderByDayOfWeekAscClassNumberAsc(1, group);
        assertAll(
                () -> assertEquals(2, classList.size()),
                () -> assertEquals(2, classList.getFirst().getGroup().size()),
                () -> assertEquals("Крылов", classList.getFirst().getUser().getFirstName())
        );
    }

    @Test
    public void ClassFindByWeekNumberAndGroupOrderByDateAscClassNumberAscScheduleTemplate()
    {
        Group group = groupDao.findById(1);
        ScheduleTemplate scheduleTemplate = new ScheduleTemplate();
        scheduleTemplate.setId(1);
        List<Class> classList = aClassDao.findByWeekNumberAndGroupOrderByDayOfWeekAscClassNumberAsc(1, group, scheduleTemplate);
        assertAll(
                () -> assertEquals(2, classList.size()),
                () -> assertEquals(2, classList.getFirst().getGroup().size()),
                () -> assertEquals("Крылов", classList.getFirst().getUser().getFirstName())
        );
    }

    @Test
    public void ClassFindById()
    {
        Class clazz = aClassDao.findById(1);
        assertAll(
                () -> assertNotNull(clazz),
                () -> assertEquals(2, clazz.getGroup().size()),
                () -> assertEquals("Крылов", clazz.getUser().getFirstName())
        );
    }

    @Test
    public void ClassDeleteById()
    {
        long classId = 1;
        aClassDao.deleteById(classId);
        assertNull(aClassDao.findById(classId));
    }

    @Test
    public void ClassSave()
    {
        List<Group> groupList = groupDao.findAllOrderByNameAsc(10, 0).getContent();
        User user = userDao.findById(1);
        Subject subject = subjectDao.findById(1);
        ScheduleTemplate scheduleTemplate = scheduleTemplateDao.findById(1);
        Class clazz = new Class();
        clazz.setWeekNumber(1);
        clazz.setClassNumber(3);
        clazz.setDayOfWeek(DayOfWeek.MONDAY);
        clazz.setAudience(256);
        clazz.setUser(user);
        clazz.setScheduleTemplate(scheduleTemplate);
        clazz.setSubject(subject);
        clazz.setGroup(groupList);
        aClassDao.save(clazz);

        long id = 1;
        Class oldClass = aClassDao.findById(id);
        oldClass.getGroup().removeFirst();
        oldClass.setAudience(234);
        aClassDao.save(oldClass);


        assertAll(
                () -> assertEquals(7, aClassDao.findByWeekNumberOrderByDayOfWeekAscClassNumberAsc(1).size()),
                () -> assertEquals(1, aClassDao.findById(id).getGroup().size())
        );
    }

    @Test
    public void AttendanceFindByStudentIdAndClassId()
    {
        assertEquals(1, attendanceDao.findByStudentIdAndClassId(4, 2).size());
    }

    @Test
    public void AttendanceFindByEducationDayDateAndEducationDaySubject()
    {
        Subject subject = subjectDao.findById(1);
        Attendance attendance = attendanceDao.findByClassDayOfWeekAndWeekNumberAndClassSubject(DayOfWeek.MONDAY, 1, subject);
        assertAll(
                () -> assertNotNull(attendance),
                () -> assertEquals(2, attendance.getClazz().getGroup().size())
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
        Class clazz = aClassDao.findById(1);
        Attendance attendance = new Attendance();
        attendance.setAttendanceStatus(attendanceStatus);
        attendance.setUser(user);
        attendance.setClazz(clazz);

        AttendanceStatus attendanceStatus1 = attendanceStatusDao.findById(3);
        Attendance oldAttendance = attendanceDao.findById(2);
        oldAttendance.setAttendanceStatus(attendanceStatus1);

        assertAll(
                () -> assertEquals(4, attendanceDao.save(attendance).getUser().getId()),
                () -> assertEquals(3, attendanceDao.save(oldAttendance).getAttendanceStatus().getId())
        );
    }

    @Test
    public void ScheduleTemplateFindAll()
    {
        assertEquals(1, scheduleTemplateDao.findAll().size());
    }

    @Test
    public void ScheduleTemplateFindById()
    {
        assertEquals("first_template" ,scheduleTemplateDao.findById(1).getName());
    }

    @Test
    public void ScheduleTemplateFindByName()
    {
        assertEquals(1 ,scheduleTemplateDao.findByName("first_template").getId());
    }

    @Test
    public void ScheduleTemplateSave()
    {
        ScheduleTemplate scheduleTemplate = new ScheduleTemplate();
        String name = "second_template";
        scheduleTemplate.setName(name);
        scheduleTemplate.setWeekCount(16);
        scheduleTemplate.setStartDate(LocalDate.now());
        scheduleTemplateDao.save(scheduleTemplate);
        assertNotNull(scheduleTemplateDao.findByName(name));
    }

    @Test
    public void ScheduleTemplateDeleteById()
    {
        assertEquals("first_template", scheduleTemplateDao.findById(1).getName());
    }
}
