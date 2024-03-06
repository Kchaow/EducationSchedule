package org.letunov.service.impl;

import org.letunov.dao.EducationDayDao;
import org.letunov.dao.GroupDao;
import org.letunov.dao.UserDao;
import org.letunov.domainModel.EducationDay;
import org.letunov.domainModel.Group;
import org.letunov.domainModel.Subject;
import org.letunov.domainModel.User;
import org.letunov.service.ScheduleService;
import org.letunov.service.dto.EducationDayDto;
import org.letunov.service.dto.ScheduleDto;
import org.letunov.service.dto.SubjectDto;
import org.letunov.service.dto.UserNamesDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@Service
public class ScheduleServiceImpl implements ScheduleService
{
    private final GroupDao groupDao;
    private final EducationDayDao educationDayDao;
    private final UserDao userDao;
    public ScheduleServiceImpl(GroupDao groupDao, EducationDayDao educationDayDao, UserDao userDao)
    {
        this.groupDao = groupDao;
        this.educationDayDao = educationDayDao;
        this.userDao = userDao;
    }

    @Override
    public ResponseEntity<ScheduleDto> getGroupSchedule(int weekNumber, String groupName)
    {
        Group group = groupDao.findByName(groupName);
        if (group == null)
            throw new NoSuchElementException("%s group doesn't exist".formatted(groupName));
        List<EducationDay> educationDays =
                educationDayDao.findByWeekNumberAndGroupOrderByDateAscClassNumberAsc(weekNumber, group);
        ScheduleDto scheduleDto = new ScheduleDto();
        Map<Long, User> userMap = new HashMap<>();
        for (EducationDay educationDay : educationDays)
        {
            UserNamesDto userNamesDto = null;
            if (educationDay.getUser() != null)
            {
                long id = educationDay.getUser().getId();
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
                    .id(educationDay.getSubject().getId())
                    .name(educationDay.getSubject().getName())
                    .build();
            List<Long> groupsId = new ArrayList<>();
            educationDay.getGroup().forEach((x) -> groupsId.add(x.getId()));
            EducationDayDto educationDayDto = EducationDayDto.builder()
                    .id(educationDay.getId())
                    .userNamesDto(userNamesDto)
                    .groupsId(groupsId)
                    .date(educationDay.getDate())
                    .classNumber(educationDay.getClassNumber())
                    .audience(educationDay.getAudience())
                    .weekNumber(educationDay.getWeekNumber())
                    .subject(subjectDto)
                    .build();
            scheduleDto.getClasses().add(educationDayDto);
        }
        return ResponseEntity.ok(scheduleDto);
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    @Override
    public ResponseEntity<String> updateSchedule(ScheduleDto scheduleDto)
    {
        if (scheduleDto == null)
            throw new NullPointerException("scheduleDto arg cannot be null");
        List<EducationDayDto> educationDayDtos = scheduleDto.getClasses();
        for (EducationDayDto educationDayDto : educationDayDtos)
        {
            EducationDay educationDay = new EducationDay();
            educationDay.setId(educationDayDto.getId());
            educationDay.setDate(educationDayDto.getDate());
            Subject subject = new Subject();
            subject.setId(educationDayDto.getSubject().getId());
            subject.setName(educationDayDto.getSubject().getName());
            educationDay.setSubject(subject);
            educationDay.setAudience(educationDayDto.getAudience());
            User user = new User();
            user.setId(educationDayDto.getId());
            educationDay.setUser(user);
            educationDay.setWeekNumber(educationDayDto.getWeekNumber());
            educationDay.setClassNumber(educationDayDto.getClassNumber());
            List<Group> groups = new ArrayList<>();
            for (Long id : educationDayDto.getGroupsId())
            {
                Group group = new Group();
                group.setId(id);
                groups.add(group);
            }
            educationDay.setGroup(groups);
            educationDay.setDate(educationDayDto.getDate());
            educationDayDao.save(educationDay);
        }
        return ResponseEntity.ok().build();
    }
}
