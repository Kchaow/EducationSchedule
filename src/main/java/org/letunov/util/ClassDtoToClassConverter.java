package org.letunov.util;

import org.letunov.dao.GroupDao;
import org.letunov.dao.ScheduleTemplateDao;
import org.letunov.dao.UserDao;
import org.letunov.domainModel.Class;
import org.letunov.domainModel.Group;
import org.letunov.domainModel.ScheduleTemplate;
import org.letunov.domainModel.User;
import org.letunov.service.dto.ClassDto;
import org.springframework.core.convert.converter.Converter;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;

public class ClassDtoToClassConverter implements Converter<ClassDto, Class>
{
    @Override
    public Class convert(ClassDto source)
    {
        Class clazz = new Class();
        clazz.setId(source.getId());
        clazz.setGroup(source.getGroups());
        clazz.setClassNumber(source.getClassNumber());
        clazz.setAudience(source.getAudience());
        clazz.setWeekNumber(source.getWeekNumber());
        User teacher = null;
        if (source.getUserNamesDto() != null)
        {
            teacher = new User();
            teacher.setId(source.getUserNamesDto().getId());
        }
        clazz.setUser(teacher);
        clazz.setDayOfWeek(DayOfWeek.of(source.getDayOfWeek()));
        ScheduleTemplate scheduleTemplate = new ScheduleTemplate();
        scheduleTemplate.setId(source.getScheduleTemplateId());
        clazz.setScheduleTemplate(scheduleTemplate);
        clazz.setSubject(source.getSubject());
        return clazz;
    }
}
