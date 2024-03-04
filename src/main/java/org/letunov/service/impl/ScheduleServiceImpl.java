package org.letunov.service.impl;

import org.letunov.dao.EducationDayDao;
import org.letunov.dao.GroupDao;
import org.letunov.dao.UserDao;
import org.letunov.domainModel.EducationDay;
import org.letunov.domainModel.Group;
import org.letunov.domainModel.User;
import org.letunov.service.ScheduleService;
import org.letunov.service.dto.EducationDayDto;
import org.letunov.service.dto.ScheduleDto;
import org.letunov.service.dto.UserNamesDto;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

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
            List<Long> groupsId = new ArrayList<>();
            educationDay.getGroup().forEach((x) -> groupsId.add(x.getId()));
            EducationDayDto educationDayDto = EducationDayDto.builder()
                    .userNamesDto(userNamesDto)
                    .groupsId(groupsId)
                    .date(educationDay.getDate())
                    .classNumber(educationDay.getClassNumber())
                    .audience(educationDay.getAudience())
                    .weekNumber(educationDay.getWeekNumber())
                    .build();
            scheduleDto.getClasses().add(educationDayDto);
        }
        return ResponseEntity.ok(scheduleDto);
    }

    @Override
    public ResponseEntity<String> updateSchedule(ScheduleDto scheduleDto)
    {
        return null;
    }
}
