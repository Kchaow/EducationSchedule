package unit.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.letunov.dao.ClassDao;
import org.letunov.dao.GroupDao;
import org.letunov.dao.ScheduleTemplateDao;
import org.letunov.dao.UserDao;
import org.letunov.domainModel.*;
import org.letunov.domainModel.Class;
import org.letunov.service.dto.ScheduleDto;
import org.letunov.service.impl.ScheduleServiceImpl;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatusCode;
import unit.DomainObjectGenerator;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ScheduleServiceTest
{
    @Mock
    UserDao userDao;
    @Mock
    GroupDao groupDao;
    @Mock
    ClassDao aClassDao;
    @Mock
    ScheduleTemplateDao scheduleTemplateDao;
    @InjectMocks
    ScheduleServiceImpl scheduleService;
    DomainObjectGenerator domainObjectGenerator = new DomainObjectGenerator();

    @BeforeEach
    public void SetupEducationDateStart()
    {
        LocalDate localDate = LocalDate.of(2024, 3, 20);
    }

    @Test
    public void getGroupScheduleTestShouldReturnSchedule()
    {
        int weekNumber = 1;
        int educationDayCount = 6;
        List<Class> classList = domainObjectGenerator.getClassList(educationDayCount);
        Group group = classList.getFirst().getGroup().getFirst();
        User user = classList.getFirst().getUser();
        int groupClassNumber = 0;
        for (Class clazz : classList) {
            if (clazz.getGroup().contains(group))
                groupClassNumber++;
        }
        final int classNumber = groupClassNumber;

        when(groupDao.findByName(anyString())).thenReturn(group);
        when(aClassDao.findByWeekNumberAndGroupOrderByDayOfWeekAscClassNumberAsc(weekNumber, group)).thenReturn(classList);
        when(userDao.findById(anyLong())).thenReturn(user);
        ScheduleTemplate scheduleTemplate = new ScheduleTemplate();
        scheduleTemplate.setId(1);
        scheduleTemplate.setActive(true);
        scheduleTemplate.setWeekCount(16);
        scheduleTemplate.setStartDate(LocalDate.of(2023, 3, 18));
        scheduleTemplate.setName("first_template");
        when(scheduleTemplateDao.findById(anyLong())).thenReturn(scheduleTemplate);

        assertAll(
                () -> assertEquals(classNumber, Objects.requireNonNull(scheduleService.getGroupSchedule(weekNumber, group.getName()).getBody()).getClasses().size()),
                () -> assertEquals(HttpStatusCode.valueOf(200), scheduleService.getGroupSchedule(weekNumber, group.getName()).getStatusCode())
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
        when(aClassDao.save(any(Class.class))).thenReturn(any(Class.class));

        int educationDayCount = 6;
        ScheduleDto scheduleDto = domainObjectGenerator.convertToScheduleDto(domainObjectGenerator.getClassList(educationDayCount));
        assertEquals(HttpStatusCode.valueOf(200), scheduleService.updateSchedule(scheduleDto).getStatusCode());
    }

    @Test
    public void updateScheduleTestShouldThrowException()
    {
        assertThrowsExactly(NullPointerException.class, () -> scheduleService.updateSchedule(null));
    }
}
