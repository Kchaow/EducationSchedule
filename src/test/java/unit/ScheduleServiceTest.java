package unit;

import com.fasterxml.jackson.databind.ObjectMapper;
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
    DomainObjectGenerator domainObjectGenerator = new DomainObjectGenerator();

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
        List<EducationDay> educationDayList = domainObjectGenerator.getEducationDayList(educationDayCount);
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
        ScheduleDto scheduleDto = domainObjectGenerator.convertToScheduleDto(domainObjectGenerator.getEducationDayList(educationDayCount));
        assertEquals(HttpStatusCode.valueOf(200), scheduleService.updateSchedule(scheduleDto).getStatusCode());
    }

    @Test
    public void updateScheduleTestShouldThrowException()
    {
        assertThrowsExactly(NullPointerException.class, () -> scheduleService.updateSchedule(null));
    }
}
