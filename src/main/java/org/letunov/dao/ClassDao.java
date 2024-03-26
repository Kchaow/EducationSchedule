package org.letunov.dao;

import org.letunov.domainModel.Class;
import org.letunov.domainModel.Group;
import org.letunov.domainModel.ScheduleTemplate;
import org.letunov.domainModel.User;

import java.time.DayOfWeek;
import java.util.List;

public interface ClassDao
{
    Class findByWeekNumberAndSubjectIdAndAudienceNumberAndDayOfWeeKAndClassNumber(int weekNumber, long subjectId, int audienceNumber, DayOfWeek dayOfWeek,
                                                                                  int classNumber, ScheduleTemplate scheduleTemplate);
    List<Class> findByWeekNumberOrderByDayOfWeekAscClassNumberAsc(int weekNumber);
    List<Class> findByWeekNumberOrderByDayOfWeekAscClassNumberAsc(int weekNumber, ScheduleTemplate scheduleTemplate);
    List<Class> findByWeekNumberAndTeacherOrderByDayOfWeekAscClassNumberAsc(int weekNumber, User user);
    List<Class> findByWeekNumberAndTeacherOrderByDayOfWeekAscClassNumberAsc(int weekNumber, User user, ScheduleTemplate scheduleTemplate);
    List<Class> findByWeekNumberAndGroupOrderByDayOfWeekAscClassNumberAsc(int weekNumber, Group group);
    List<Class> findByWeekNumberAndGroupOrderByDayOfWeekAscClassNumberAsc(int weekNumber, Group group, ScheduleTemplate scheduleTemplate);
    Class findById(long id);
    void deleteById(long id);
    Class save(Class clazz);
}
