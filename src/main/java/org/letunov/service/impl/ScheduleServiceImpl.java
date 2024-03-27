package org.letunov.service.impl;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.letunov.dao.*;
import org.letunov.domainModel.*;
import org.letunov.domainModel.Class;
import org.letunov.service.ScheduleService;
import org.letunov.service.dto.ClassDto;
import org.letunov.service.dto.ScheduleDto;
import org.letunov.service.dto.SubjectDto;
import org.letunov.service.dto.UserNamesDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.ConversionService;
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
@Slf4j
public class ScheduleServiceImpl implements ScheduleService
{
    private final ConversionService conversionService;
    private final GroupDao groupDao;
    private final ClassDao aClassDao;
    private final UserDao userDao;
    private final ScheduleTemplateDao scheduleTemplateDao;
    private final SubjectDao subjectDao;
    public ScheduleServiceImpl(GroupDao groupDao, ClassDao aClassDao, UserDao userDao, ScheduleTemplateDao scheduleTemplateDao, SubjectDao subjectDao, ConversionService conversionService)
    {
        this.conversionService = conversionService;
        this.subjectDao = subjectDao;
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
//            SubjectDto subjectDto = SubjectDto.builder()
//                    .id(clazz.getSubject().getId())
//                    .name(clazz.getSubject().getName())
//                    .build();
//            List<Long> groupsId = new ArrayList<>();
//            clazz.getGroup().forEach((x) -> groupsId.add(x.getId()));
            ClassDto educationDayDto = ClassDto.builder()
                    .id(clazz.getId())
                    .userNamesDto(userNamesDto)
                    .groups(clazz.getGroup())
                    .dayOfWeek(clazz.getDayOfWeek().getValue())
                    .classNumber(clazz.getClassNumber())
                    .audience(clazz.getAudience())
                    .weekNumber(clazz.getWeekNumber())
                    .subject(clazz.getSubject())
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
        return new ResponseEntity<ScheduleDto>(scheduleDto, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<ClassDto> detachClass(long classId, long groupId)
    {
        deleteClass(classId, groupId);
        Class newClass = aClassDao.findById(classId);
        List<Group> groups = new ArrayList<>();
        groups.add(groupDao.findById(groupId));
        newClass.setGroup(groups);
        newClass.setId(0);
        Class saved = aClassDao.save(newClass);
        return ResponseEntity.ok(conversionService.convert(saved, ClassDto.class));
    }

    @Override
    public ResponseEntity<ScheduleDto> getGroupSchedule(int weekNumber, String groupName, long templateId)
    {
        Group group = groupDao.findByName(groupName);
        if (group == null)
            throw new NoSuchElementException("%s group doesn't exist".formatted(groupName));
        ScheduleTemplate scheduleTemplate = scheduleTemplateDao.findById(templateId);
        if (scheduleTemplate == null)
            throw new NoSuchElementException("%d id scheduleTemplate doesn't exist".formatted(templateId));
        List<Class> classes =
                aClassDao.findByWeekNumberAndGroupOrderByDayOfWeekAscClassNumberAsc(weekNumber, group, scheduleTemplate);
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
//            SubjectDto subjectDto = SubjectDto.builder()
//                    .id(clazz.getSubject().getId())
//                    .name(clazz.getSubject().getName())
//                    .build();
//            List<Long> groupsId = new ArrayList<>();
//            clazz.getGroup().forEach((x) -> groupsId.add(x.getId()));
            ClassDto educationDayDto = ClassDto.builder()
                    .id(clazz.getId())
                    .userNamesDto(userNamesDto)
                    .groups(clazz.getGroup())
                    .dayOfWeek(clazz.getDayOfWeek().getValue())
                    .classNumber(clazz.getClassNumber())
                    .audience(clazz.getAudience())
                    .weekNumber(clazz.getWeekNumber())
                    .subject(clazz.getSubject())
                    .build();
            classesDto.add(educationDayDto);
        }

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        List<String> dates = new LinkedList<>();
        int educationDayCount = 6;
        LocalDate educationDateStart = scheduleTemplateDao.findById(templateId).getStartDate();
        log.info(educationDateStart.toString());
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
//            List<Group> groups = new ArrayList<>();
//            for (Long id : educationDayDto.getGroupsId())
//            {
//                Group group = new Group();
//                group.setId(id);
//                groups.add(group);
//            }
            clazz.setGroup(educationDayDto.getGroups());
            clazz.setDayOfWeek(DayOfWeek.of(educationDayDto.getDayOfWeek()));
            aClassDao.save(clazz);
        }
        return ResponseEntity.ok().build();
    }

    @Transactional
    @Override
    public ResponseEntity<ClassDto> saveOrUpdateClass(ClassDto classDto, long templateId)
    {
        log.info(classDto.toString());
        ScheduleTemplate scheduleTemplate = scheduleTemplateDao.findById(templateId);
        if (scheduleTemplate == null)
            throw new NoSuchElementException("%d id scheduleTemplate doesn't exist".formatted(templateId));
        if (classDto.getSubject() == null)
            throw new NullPointerException("subjectDto of classDto was null");
        Subject subject = subjectDao.findById(classDto.getSubject().getId());
        if (subject == null)
            throw new NoSuchElementException("%d id subject doesn't exist".formatted(classDto.getSubject().getId()));
        User teacher = classDto.getUserNamesDto() != null ? userDao.findById(classDto.getUserNamesDto().getId()) : null;
        if (classDto.getUserNamesDto() != null && teacher == null)
            throw new NoSuchElementException("%d id user doesn't exist".formatted(classDto.getUserNamesDto().getId()));
        if (classDto.getDayOfWeek() > 7 || classDto.getDayOfWeek() < 1)
            throw new IndexOutOfBoundsException("day of week index must be in range 1..7");
        if (classDto.getWeekNumber() < 1)
            throw new IndexOutOfBoundsException("weekNumber cannot be less or equal 0");
        if (classDto.getClassNumber() < 1 || classDto.getClassNumber() > 6)
            throw new IndexOutOfBoundsException("classNumber must be in range 1..6");
        Group group = groupDao.findById(classDto.getGroups().getFirst().getId());
        if (group == null)
            throw new NoSuchElementException("%d id group doesn't exist".formatted(classDto.getGroups().getFirst().getId()));



        Class clazz = conversionService.convert(classDto, Class.class);
        List<Class> similarClasses = aClassDao.findByWeekNumberAndSubjectIdAndAudienceNumberAndDayOfWeeKAndClassNumber(classDto.getWeekNumber(), classDto.getSubject().getId(), classDto.getAudience(),
                DayOfWeek.of(classDto.getDayOfWeek()), classDto.getClassNumber(), scheduleTemplate);
        log.info(similarClasses.toString());
        Class existingClass = null;
        for (Class similarClass : similarClasses)
        {
            if (!similarClass.equals(clazz))
            {
                existingClass = similarClass;
                break;
            }
        }
        if (existingClass != null)
        {
            if (!existingClass.getGroup().contains(group))
            {
                existingClass.getGroup().add(group);
            }
            existingClass.setUser(teacher);
            if (classDto.getId() > 0)
                aClassDao.deleteById(classDto.getId());
            ClassDto saved = conversionService.convert(aClassDao.save(existingClass), ClassDto.class);
            return new ResponseEntity<ClassDto>(saved, HttpStatus.OK);
        }


        if (classDto.getId() > 0)
        {
            clazz.setGroup(aClassDao.findById(classDto.getId()).getGroup());
        }
        ClassDto saved = conversionService.convert(aClassDao.save(clazz), ClassDto.class);
        return new ResponseEntity<ClassDto>(saved, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Object> deleteClass(long classId, long groupId)
    {
        Class clazz = aClassDao.findById(classId);
        if (clazz.getGroup().size() > 1)
        {
            Group group = groupDao.findById(groupId);
            clazz.getGroup().remove(group);
            aClassDao.save(clazz);
            return ResponseEntity.noContent().build();
        }
        aClassDao.deleteById(classId);
        return ResponseEntity.noContent().build();
    }
}
