package org.letunov.service.impl;

import lombok.Setter;
import org.letunov.dao.ClassDao;
import org.letunov.dao.GroupDao;
import org.letunov.dao.ScheduleTemplateDao;
import org.letunov.dao.UserDao;
import org.letunov.domainModel.*;
import org.letunov.domainModel.Class;
import org.letunov.service.ScheduleService;
import org.letunov.service.dto.ClassDto;
import org.letunov.service.dto.ScheduleDto;
import org.letunov.service.dto.SubjectDto;
import org.letunov.service.dto.UserNamesDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class ScheduleServiceImpl implements ScheduleService
{
    private final GroupDao groupDao;
    private final ClassDao aClassDao;
    private final UserDao userDao;
    private final ScheduleTemplateDao scheduleTemplateDao;
    public ScheduleServiceImpl(GroupDao groupDao, ClassDao aClassDao, UserDao userDao, ScheduleTemplateDao scheduleTemplateDao)
    {
        this.groupDao = groupDao;
        this.aClassDao = aClassDao;
        this.userDao = userDao;
        this.scheduleTemplateDao = scheduleTemplateDao;
    }

    @Override
    public ResponseEntity<ScheduleDto> getGroupSchedule(int weekNumber, String groupName)
    {
        Group group = groupDao.findByName(groupName);
        if (group == null)
            throw new NoSuchElementException("%s group doesn't exist".formatted(groupName));
        List<Class> classes =
                aClassDao.findByWeekNumberAndGroupOrderByDayOfWeekAscClassNumberAsc(weekNumber, group);
        ScheduleDto scheduleDto = new ScheduleDto();
        Map<Long, User> userMap = new HashMap<>();
        List<ClassDto> classesDto = new LinkedList<>();
        for (Class clazz : classes)
        {
            UserNamesDto userNamesDto = null;
            if (clazz.getUser() != null)
            {
                long id = clazz.getUser().getId();
                User user = userMap.get(id);
                if (user == null)
                {
                    user = userDao.findById(id);
                    userMap.put(id, user);
                }
                userNamesDto = UserNamesDto.builder()
                        .id(id)
                        .firstName(user.getFirstName())
                        .lastName(user.getLastName())
                        .middleName(user.getMiddleName())
                        .build();
            }
            SubjectDto subjectDto = SubjectDto.builder()
                    .id(clazz.getSubject().getId())
                    .name(clazz.getSubject().getName())
                    .build();
            List<Long> groupsId = new ArrayList<>();
            clazz.getGroup().forEach((x) -> groupsId.add(x.getId()));
            ClassDto educationDayDto = ClassDto.builder()
                    .id(clazz.getId())
                    .userNamesDto(userNamesDto)
                    .groupsId(groupsId)
                    .dayOfWeek(clazz.getDayOfWeek().getValue())
                    .classNumber(clazz.getClassNumber())
                    .audience(clazz.getAudience())
                    .weekNumber(clazz.getWeekNumber())
                    .subject(subjectDto)
                    .build();
            classesDto.add(educationDayDto);
        }

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        List<String> dates = new LinkedList<>();
        int educationDayCount = 6;
        LocalDate educationDateStart = aClassDao.findByWeekNumberAndGroupOrderByDayOfWeekAscClassNumberAsc(1, group).getFirst().getScheduleTemplate().getStartDate();
        for (int i = 0; i < educationDayCount; i++)
        {
            if (weekNumber == 1 && i + 1 < educationDateStart.getDayOfWeek().getValue())
            {
                dates.add("");
            }
            else if (weekNumber == 1)
            {
                LocalDate date = educationDateStart.plusDays(i - (educationDateStart.getDayOfWeek().getValue() - 1));
                dates.add(date.format(dateTimeFormatter));
            }
            else
            {
                int daysInWeek = 7;
                LocalDate date = educationDateStart.plusDays(i+ (long) daysInWeek *(weekNumber-1)-(educationDateStart.getDayOfWeek().getValue()-1));
                dates.add(date.format(dateTimeFormatter));
            }
        }
        scheduleDto.setDates(dates);
        scheduleDto.setClasses(classesDto);

//        HttpHeaders httpHeaders = new HttpHeaders();
//        httpHeaders.setAccessControlAllowOrigin("*");
        return new ResponseEntity<ScheduleDto>(scheduleDto, HttpStatus.OK);
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    @Override
    public ResponseEntity<String> updateSchedule(ScheduleDto scheduleDto)
    {
        if (scheduleDto == null)
            throw new NullPointerException("scheduleDto arg cannot be null");
        List<ClassDto> classesDtos = scheduleDto.getClasses();
        for (ClassDto educationDayDto : classesDtos)
        {
            Class clazz = new Class();
            clazz.setId(educationDayDto.getId());
            clazz.setDayOfWeek(DayOfWeek.of(educationDayDto.getDayOfWeek()));
            Subject subject = new Subject();
            subject.setId(educationDayDto.getSubject().getId());
            subject.setName(educationDayDto.getSubject().getName());
            clazz.setSubject(subject);
            clazz.setAudience(educationDayDto.getAudience());
            User user = new User();
            user.setId(educationDayDto.getId());
            clazz.setUser(user);
            clazz.setWeekNumber(educationDayDto.getWeekNumber());
            clazz.setClassNumber(educationDayDto.getClassNumber());
            List<Group> groups = new ArrayList<>();
            for (Long id : educationDayDto.getGroupsId())
            {
                Group group = new Group();
                group.setId(id);
                groups.add(group);
            }
            clazz.setGroup(groups);
            clazz.setDayOfWeek(DayOfWeek.of(educationDayDto.getDayOfWeek()));
            aClassDao.save(clazz);
        }
        return ResponseEntity.ok().build();
    }
}
