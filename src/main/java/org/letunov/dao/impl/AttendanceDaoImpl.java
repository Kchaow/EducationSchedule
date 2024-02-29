package org.letunov.dao.impl;

import org.letunov.dao.AttendanceDao;
import org.letunov.domainModel.Attendance;
import org.letunov.domainModel.Subject;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class AttendanceDaoImpl implements AttendanceDao
{
    private final JdbcTemplate jdbcTemplate;
    public AttendanceDaoImpl(JdbcTemplate jdbcTemplate)
    {
        this.jdbcTemplate = jdbcTemplate;
    }
    @Override
    public List<Attendance> findByStudentIdAndEducationDayId(long userId, long educationDayId)
    {
        final String query = """
                                       SELECT a.id, d.id class_id, d.week_number, d.user_id, d.date,
                                       d.class_number
                        """;
        return null;
    }

    @Override
    public List<Attendance> findByEducationDayDateAndEducationDaySubject(LocalDate date, Subject subject)
    {
        return null;
    }

    @Override
    public Attendance findById(long id)
    {
        return null;
    }

    @Override
    public void deleteById(long id)
    {

    }

    @Override
    public Attendance save(Attendance attendance)
    {
        return null;
    }

    private static class AttendanceRowMapper implements RowMapper<Attendance>
    {

        @Override
        public Attendance mapRow(ResultSet rs, int rowNum) throws SQLException
        {
            return null;
        }
    }
}
