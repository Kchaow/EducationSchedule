package unit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.letunov.dao.EducationDayDao;
import org.letunov.dao.GroupDao;
import org.letunov.dao.UserDao;
import org.letunov.domainModel.*;
import org.letunov.service.ScheduleService;
import org.letunov.service.dto.EducationDayDto;
import org.letunov.service.dto.ScheduleDto;
import org.letunov.service.dto.SubjectDto;
import org.letunov.service.dto.UserNamesDto;
import org.letunov.service.impl.ScheduleServiceImpl;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatusCode;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
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
        when(groupDao.findByName(anyString())).thenReturn(group);

        int weekNumber = 1;
        int educationDayCount = 6;
        List<EducationDay> educationDayList = getEducationDayList(educationDayCount);
        when(educationDayDao.findByWeekNumberAndGroupOrderByDateAscClassNumberAsc(weekNumber, group)).thenReturn(educationDayList);

        User user = educationDayList.getFirst().getUser();
        when(userDao.findById(anyLong())).thenReturn(user);

        assertAll(
                () -> assertEquals(educationDayCount, Objects.requireNonNull(scheduleService.getGroupSchedule(weekNumber, groupName).getBody()).getClasses().size()),
                () -> assertEquals(HttpStatusCode.valueOf(200), scheduleService.getGroupSchedule(weekNumber, groupName).getStatusCode())
        );
    }

    @Test
    public void getGroupScheduleTestShouldThrowException()
    {
        when(groupDao.findByName(anyString())).thenReturn(null);

        assertThrowsExactly(NoSuchElementException.class, () -> scheduleService.getGroupSchedule(1, "groupName"));
    }

    @Test
    public void updateScheduleTestShouldReturnOk()
    {
        when(educationDayDao.save(any(EducationDay.class))).thenReturn(any(EducationDay.class));

        int educationDayCount = 6;
        ScheduleDto scheduleDto = convertToScheduleDto(getEducationDayList(educationDayCount));
        assertEquals(HttpStatusCode.valueOf(200), scheduleService.updateSchedule(scheduleDto).getStatusCode());
    }

    @Test
    public void updateScheduleTestShouldThrowException()
    {
        assertThrowsExactly(NullPointerException.class, () -> scheduleService.updateSchedule(null));
    }

    private ScheduleDto convertToScheduleDto(List<EducationDay> educationDayList)
    {
        ScheduleDto scheduleDto = new ScheduleDto();
        List<EducationDayDto> educationDayDtoList = new ArrayList<>();
        for (EducationDay educationDay : educationDayList)
        {
            List<Long> groupIds = new ArrayList<>();
            educationDay.getGroup().forEach(x -> groupIds.add(x.getId()));
            SubjectDto subjectDto = new SubjectDto();
            subjectDto.setId(educationDay.getSubject().getId());
            subjectDto.setName(educationDay.getSubject().getName());
            UserNamesDto userNamesDto = new UserNamesDto();
            userNamesDto.setId(educationDay.getUser().getId());
            userNamesDto.setFirstName(educationDay.getUser().getFirstName());
            userNamesDto.setLastName(educationDay.getUser().getLastName());
            userNamesDto.setMiddleName(educationDay.getUser().getMiddleName());
            EducationDayDto educationDayDto = EducationDayDto.builder()
                    .id(educationDay.getId())
                    .userNamesDto(userNamesDto)
                    .subject(subjectDto)
                    .audience(educationDay.getAudience())
                    .classNumber(educationDay.getClassNumber())
                    .groupsId(groupIds)
                    .date(educationDay.getDate())
                    .weekNumber(educationDay.getWeekNumber())
                    .build();
            educationDayDtoList.add(educationDayDto);
        }
        scheduleDto.setClasses(educationDayDtoList);
        return scheduleDto;
    }

    private List<EducationDay> getEducationDayList(int size)
    {
        List<EducationDay> educationDayList = new ArrayList<>();

        Role role = new Role();
        role.setId(3);
        role.setName("teacher");

        User user = new User();
        user.setId(2);
        user.setFirstName("firstName");
        user.setLastName("lastName");
        user.setMiddleName("middleName");
        user.setLogin("login");
        user.setEmail("email");
        user.setPassword("password");
        user.setGroup(null);
        user.setRole(role);

        Group group = new Group();
        group.setId(1);
        group.setName("group");
        List<Group> groups = new ArrayList<>();
        groups.add(group);

        int weekNumber = 1;
        int maxClass = 6;

        Subject subject1 = new Subject();
        subject1.setId(1);
        subject1.setName("firstSubject");

        Subject subject2 = new Subject();
        subject2.setId(2);
        subject2.setName("secondSubject");

        LocalDate date = LocalDate.of(2024, 3, 4);
        for (int i = 0, classNumber = 0; i < size; i++, classNumber++)
        {
            if (date.getDayOfWeek() == DayOfWeek.SUNDAY)
            {
                weekNumber++;
                date = date.plusDays(1);
            }
            if (classNumber > maxClass)
                classNumber = 1;
            EducationDay educationDay = new EducationDay();
            educationDay.setId(i+1);
            educationDay.setWeekNumber(weekNumber);
            educationDay.setDate(date);
            educationDay.setUser(user);
            educationDay.setAudience(255 + i);
            educationDay.setClassNumber(classNumber);
            educationDay.setGroup(groups);
            if (i % 2 == 0)
                educationDay.setSubject(subject1);
            else
                educationDay.setSubject(subject2);
            date = date.plusDays(1);
            educationDayList.add(educationDay);
        }
        return educationDayList;
    }
}
