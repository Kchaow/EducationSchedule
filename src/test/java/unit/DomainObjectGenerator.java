package unit;

import org.letunov.domainModel.*;
import org.letunov.service.dto.EducationDayDto;
import org.letunov.service.dto.ScheduleDto;
import org.letunov.service.dto.SubjectDto;
import org.letunov.service.dto.UserNamesDto;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

//Сделать приемлемым
public class DomainObjectGenerator
{
    public ScheduleDto convertToScheduleDto(List<EducationDay> educationDayList)
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

    public List<EducationDay> getEducationDayList(int size)
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
