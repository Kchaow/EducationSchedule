package org.letunov.dao.impl;

import lombok.Getter;
import lombok.Setter;
import org.letunov.dao.AttendanceDao;
import org.letunov.dao.AttendanceStatusDao;
import org.letunov.dao.EducationDayDao;
import org.letunov.dao.UserDao;
import org.letunov.domainModel.*;
import org.letunov.exceptions.TheDependentEntityIsPreservedBeforeTheIndependentEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Repository
@Transactional
public class AttendanceDaoImpl implements AttendanceDao
{
    private final JdbcTemplate jdbcTemplate;
    private final EducationDayDao educationDayDao;
    private final UserDao userDao;
    private final AttendanceStatusDao attendanceStatusDao;
    public AttendanceDaoImpl(JdbcTemplate jdbcTemplate, EducationDayDao educationDayDao, UserDao userDao, AttendanceStatusDao attendanceStatusDao)
    {
        this.jdbcTemplate = jdbcTemplate;
        this.educationDayDao = educationDayDao;
        this.attendanceStatusDao = attendanceStatusDao;
        this.userDao = userDao;
    }
    @Override
    @Transactional(readOnly = true)
    public List<Attendance> findByStudentIdAndEducationDayId(long userId, long educationDayId)
    {
        final String query = "SELECT id, attendance_status_id, user_id, education_day_id FROM attendance WHERE user_id = ? AND education_day_id = ?";
        List<Attendance> attendances = jdbcTemplate.query(query, new AttendanceRowMapper(), userId, educationDayId);
        fillDependence(attendances);
        return attendances;
    }

    @Override
    @Transactional(readOnly = true)
    public Attendance findByEducationDayDateAndEducationDaySubject(LocalDate date, Subject subject)
    {
        if (date == null)
            throw new NullPointerException("date arg cannot be null");
        if (subject == null)
            throw new NullPointerException("subject arg cannot be null");
        final String query = """
                SELECT att.id, attendance_status_id, att.user_id, education_day_id, "date", subject_id
                FROM attendance att
                LEFT JOIN education_day ed ON education_day_id = ed.id
                WHERE "date" = ? AND subject_id = ?
                """;
        List<Attendance> attendances = jdbcTemplate.query(query, new AttendanceRowMapper(), date, subject.getId());
        fillDependence(attendances);
        if (attendances.isEmpty())
            return null;
        return attendances.getFirst();
    }

    @Override
    @Transactional(readOnly = true)
    public Attendance findById(long id)
    {
        final String query = "SELECT id, attendance_status_id, user_id, education_day_id FROM attendance WHERE id = ?";
        List<Attendance> attendances = jdbcTemplate.query(query, new AttendanceRowMapper(), id);
        fillDependence(attendances);
        if (attendances.isEmpty())
            return null;
        return attendances.getFirst();
    }

    @Override
    @Transactional
    public void deleteById(long id)
    {
        final String query = "DELETE FROM attendance WHERE id = ?";
        jdbcTemplate.update(query, id);
    }

    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public Attendance save(Attendance attendance)
    {
        if (attendance == null)
            throw new NullPointerException("attendance cannot be null");
        else if (attendance.getEducationDay() == null)
            throw new NullPointerException("attendance educationDay cannot be null");
        else if (attendance.getUser() == null)
            throw new NullPointerException("attendance educationDay cannot be null");

        if (userDao.findById(attendance.getUser().getId()) == null)
            throw new TheDependentEntityIsPreservedBeforeTheIndependentEntity("Trying to save a dependent attendance entity before an independent user entity");
        if (educationDayDao.findById(attendance.getEducationDay().getId()) == null)
            throw new TheDependentEntityIsPreservedBeforeTheIndependentEntity("Trying to save a dependent attendance entity before an independent education entity");
        if (attendance.getId() != 0 && findById(attendance.getId()) != null)
        {
            final String query = "UPDATE attendance SET attendance_status_id = ?, user_id = ?, education_day_id = ? WHERE id = ?";
            jdbcTemplate.update(query, attendance.getAttendanceStatus() == null ? null : attendance.getAttendanceStatus().getId(), attendance.getUser().getId(), attendance.getEducationDay().getId(), attendance.getId());
            return findById(attendance.getId());
        }
        final String query = """
                INSERT INTO attendance(attendance_status_id, user_id, education_day_id)
                VALUES(?,?,?);
                """;
        KeyHolder keyHolder = new GeneratedKeyHolder();
        PreparedStatementSaveCreator preparedStatementSaveCreator = new PreparedStatementSaveCreator();
        preparedStatementSaveCreator.setAttendance(attendance);
        preparedStatementSaveCreator.setQuery(query);
        jdbcTemplate.update(preparedStatementSaveCreator, keyHolder);
        return findById(Objects.requireNonNull(keyHolder.getKey()).longValue());
    }

    private List<Attendance> fillDependence(List<Attendance> attendances)
    {
        for (Attendance attendance : attendances)
        {
            User user = userDao.findById(attendance.getUser().getId());
            attendance.setUser(user);
            if (attendance.getAttendanceStatus() != null)
            {
                AttendanceStatus attendanceStatus = attendanceStatusDao.findById(attendance.getAttendanceStatus().getId());
                attendance.setAttendanceStatus(attendanceStatus);
            }
            EducationDay educationDay = educationDayDao.findById(attendance.getEducationDay().getId());
            attendance.setEducationDay(educationDay);
        }
        return attendances;
    }

    private static class AttendanceRowMapper implements RowMapper<Attendance>
    {

        @Override
        public Attendance mapRow(ResultSet rs, int rowNum) throws SQLException
        {
            Attendance attendance = new Attendance();
            long attendanceStatusId = rs.getLong("attendance_status_id");
            if (attendanceStatusId != 0)
            {
                AttendanceStatus attendanceStatus = new AttendanceStatus();
                attendanceStatus.setId(attendanceStatusId);
                attendance.setAttendanceStatus(attendanceStatus);
            }
            attendance.setId(rs.getLong("id"));
            User user = new User();
            user.setId(rs.getLong("user_id"));
            attendance.setUser(user);
            EducationDay educationDay = new EducationDay();
            educationDay.setId(rs.getLong("education_day_id"));
            attendance.setEducationDay(educationDay);
            return attendance;
        }
    }

    @Setter
    @Getter
    private static class PreparedStatementSaveCreator implements PreparedStatementCreator
    {
        private Attendance attendance;
        private String query;
        @Override
        public PreparedStatement createPreparedStatement(Connection con) throws SQLException
        {
            PreparedStatement preparedStatement = con.prepareStatement(query, new String[] {"id"});
            if (attendance.getAttendanceStatus() == null)
                preparedStatement.setNull(1, Types.NULL);
            else
                preparedStatement.setLong(1, attendance.getAttendanceStatus().getId());
            preparedStatement.setLong(2, attendance.getUser().getId());
            preparedStatement.setLong(3, attendance.getEducationDay().getId());
            return preparedStatement;
        }
    }
}
