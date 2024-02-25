package org.letunov.dao.impl;

import org.letunov.dao.AttendanceStatusDao;
import org.letunov.domainModel.AttendanceStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class AttendanceStatusDaoImpl implements AttendanceStatusDao
{
    private final JdbcTemplate jdbcTemplate;

    public AttendanceStatusDaoImpl(JdbcTemplate jdbcTemplate)
    {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<AttendanceStatus> findAll()
    {
        final String query = "SELECT id, name FROM attendance_status";
        RowMapper<AttendanceStatus> rowMapper = new AttendanceStatusRowMapper();
        return jdbcTemplate.query(query, rowMapper);
    }

    @Override
    public AttendanceStatus findById(long id) {
        return null;
    }

    @Override
    public AttendanceStatus findByName(String name) {
        return null;
    }

    private static class AttendanceStatusRowMapper implements RowMapper<AttendanceStatus>
    {
        @Override
        public AttendanceStatus mapRow(ResultSet rs, int rowNum) throws SQLException
        {
            AttendanceStatus attendanceStatus = new AttendanceStatus();
            attendanceStatus.setName(rs.getString("name"));
            attendanceStatus.setId(rs.getInt("id"));
            return attendanceStatus;
        }
    }
}
