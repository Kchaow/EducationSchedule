package org.letunov.dao.impl;

import org.letunov.dao.AttendanceStatusDao;
import org.letunov.domainModel.AttendanceStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import javax.sql.RowSet;
import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
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
    public AttendanceStatus findById(long id)
    {
        final String query = "SELECT id, name FROM attendance_status WHERE id = ?";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(query, id);
        AttendanceStatus attendanceStatus = new AttendanceStatus();
        if (rowSet.first())
        {
            rowSet.first();
            attendanceStatus.setId(rowSet.getInt(1));
            attendanceStatus.setName(rowSet.getString(2));
            return attendanceStatus;
        }
        return null;
    }

    @Override
    public AttendanceStatus findByName(String name)
    {
        if (name == null)
            throw new NullPointerException("name arg cannot be null");
        final String query = "SELECT id, name FROM attendance_status WHERE name = ?";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(query, name);
        AttendanceStatus attendanceStatus = new AttendanceStatus();
        if (rowSet.first())
        {
            rowSet.first();
            attendanceStatus.setId(rowSet.getInt(1));
            attendanceStatus.setName(rowSet.getString(2));
            return attendanceStatus;
        }
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
