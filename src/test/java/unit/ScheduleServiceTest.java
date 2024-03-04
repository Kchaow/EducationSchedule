package unit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.letunov.dao.EducationDayDao;
import org.letunov.dao.GroupDao;
import org.letunov.dao.UserDao;
import org.letunov.domainModel.*;
import org.letunov.service.ScheduleService;
import org.letunov.service.impl.ScheduleServiceImpl;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatusCode;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ScheduleServiceTest
{
    @Mock
    UserDao userDao;
    @Mock
    GroupDao groupDao;
    @Mock
    EducationDayDao educationDayDao;
    @InjectMocks
    ScheduleServiceImpl scheduleService;

    @Test
    public void getGroupScheduleTestShouldReturnSchedule()
    {
        String groupName = "БСБО-02-21";
        long groupId = 2;
        Group group = new Group();
        group.setId(groupId);
        group.setName(groupName);
        when(groupDao.findByName(groupName)).thenReturn(group);

        int weekNumber = 1;
        List<EducationDay> educationDayList = getEducationDayList();
        when(educationDayDao.findByWeekNumberAndGroupOrderByDateAscClassNumberAsc(weekNumber, group)).thenReturn(educationDayList);

        User user = educationDayList.getFirst().getUser();
        long userId = user.getId();
        when(userDao.findById(userId)).thenReturn(user);

        assertAll(
                () -> assertEquals(4, Objects.requireNonNull(scheduleService.getGroupSchedule(weekNumber, groupName).getBody()).getClasses().size()),
                () -> assertEquals(HttpStatusCode.valueOf(200), scheduleService.getGroupSchedule(weekNumber, groupName).getStatusCode())
        );
    }

    @Test
    public void getGroupScheduleTestShouldThrowException()
    {
        String groupName = "БСБО-02-21";
        when(groupDao.findByName(groupName)).thenReturn(null);

        assertThrowsExactly(NoSuchElementException.class, () -> scheduleService.getGroupSchedule(1, groupName));
    }

    private List<EducationDay> getEducationDayList()
    {
        List<EducationDay> educationDayList = new ArrayList<>();

        Role role = new Role();
        role.setId(3);
        role.setName("teacher");

        User user = new User();
        user.setId(2);
        user.setFirstName("Степан");
        user.setLastName("Крылов");
        user.setMiddleName("Адамович");
        user.setLogin("login");
        user.setEmail("email");
        user.setPassword("password");
        user.setGroup(null);
        user.setRole(role);

        Group group1 = new Group();
        group1.setId(1);
        group1.setName("БСБО-01-21");
        Group group2 = new Group();
        group2.setId(2);
        group2.setName("БСБО-02-21");
        List<Group> groups = new ArrayList<>();
        groups.add(group1);
        groups.add(group2);

        Subject subject1 = new Subject();
        subject1.setId(1);
        subject1.setName("Математические модели и методы безопасного функционирования компонент программного обеспечения");

        Subject subject2 = new Subject();
        subject2.setId(3);
        subject2.setName("Методы и средства взаимодействия компонент программного обеспечения");

        EducationDay educationDay1 = new EducationDay();
        educationDay1.setId(1);
        educationDay1.setWeekNumber(1);
        educationDay1.setDate(LocalDate.of(2024, 2, 19));
        educationDay1.setUser(user);
        educationDay1.setAudience(255);
        educationDay1.setClassNumber(1);
        educationDay1.setDayOfWeek(DayOfWeek.MONDAY);
        educationDay1.setGroup(groups);
        educationDay1.setSubject(subject1);

        EducationDay educationDay2 = new EducationDay();
        educationDay2.setId(2);
        educationDay2.setDate(LocalDate.of(2024, 2, 19));
        educationDay2.setWeekNumber(1);
        educationDay2.setUser(user);
        educationDay2.setAudience(255);
        educationDay2.setGroup(groups);
        educationDay2.setClassNumber(2);
        educationDay2.setDayOfWeek(DayOfWeek.MONDAY);
        educationDay2.setSubject(subject1);

        EducationDay educationDay3 = new EducationDay();
        educationDay3.setId(5);
        educationDay3.setDate(LocalDate.of(2024, 2, 21));
        educationDay3.setWeekNumber(1);
        educationDay3.setClassNumber(5);
        List<Group> singleGroup = new ArrayList<>();
        singleGroup.add(group2);
        educationDay3.setGroup(singleGroup);
        educationDay3.setSubject(subject2);
        educationDay3.setAudience(249);

        EducationDay educationDay4 = new EducationDay();
        educationDay4.setId(6);
        educationDay4.setDate(LocalDate.of(2024, 2, 21));
        educationDay4.setWeekNumber(1);
        educationDay4.setClassNumber(6);
        singleGroup.add(group2);
        educationDay4.setGroup(singleGroup);
        educationDay4.setSubject(subject2);
        educationDay4.setAudience(249);

        educationDayList.add(educationDay1);
        educationDayList.add(educationDay2);
        educationDayList.add(educationDay3);
        educationDayList.add(educationDay4);
        return educationDayList;
    }
}
