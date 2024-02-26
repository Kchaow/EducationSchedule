package org.letunov.dao;

import org.letunov.domainModel.Attendance;
import org.letunov.domainModel.Subject;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

public interface AttendanceDao
{
    List<Attendance> findByStudentIdAndEducationDayId(long user_id, long education_day_id);
    List<Attendance> findByEducationDayDateAndEducationDaySubject(LocalDate date, Subject subject);
    Attendance findById(long id);
    void deleteById(long id);
    Attendance save(Attendance attendance);
}