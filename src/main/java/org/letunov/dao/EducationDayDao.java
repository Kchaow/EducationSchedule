package org.letunov.dao;

import org.letunov.domainModel.EducationDay;
import org.letunov.domainModel.Group;
import org.letunov.domainModel.User;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface EducationDayDao
{
    List<EducationDay> findByWeekNumberOrderByDateAscClassNumberAsc(int weekNumber);
    List<EducationDay> findByWeekNumberAndTeacherOrderByDateAscClassNumberAsc(int weekNumber, User user);
    List<EducationDay> findByWeekNumberAndGroupOrderByDateAscClassNumberAsc(int weekNumber, Group group);
    EducationDay findById(long id);
    void deleteById(long id);
    EducationDay save(EducationDay educationDay);
}
