package org.letunov.dao;

import org.letunov.domainModel.EducationDay;
import org.letunov.domainModel.Group;
import org.letunov.domainModel.User;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface EducationDayDao
{
    List<EducationDay> findByWeekNumberOrderByDayOfWeekAscClassNumberAsc(int weekNumber);
    List<EducationDay> findByWeekNumberAndTeacherOrderByDayOfWeekAscClassNumberAsc(int weekNumber, User user);
    List<EducationDay> findByWeekNumberAndGroupOrderByDayOfWeekAscClassNumberAsc(int weekNumber, Group group);
    EducationDay findById(long id);
    void deleteById(long id);
    EducationDay save(EducationDay educationDay);
}
