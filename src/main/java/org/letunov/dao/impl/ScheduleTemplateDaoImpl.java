package org.letunov.dao.impl;

import org.letunov.dao.ScheduleTemplateDao;
import org.letunov.domainModel.Group;
import org.letunov.domainModel.ScheduleTemplate;
import org.springframework.dao.EmptyResultDataAccessException;
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
import java.time.ZoneId;
import java.util.List;
import java.util.Objects;
import java.sql.Date;

@Transactional
@Repository
public class ScheduleTemplateDaoImpl implements ScheduleTemplateDao
{
    private final JdbcTemplate jdbcTemplate;

    public ScheduleTemplateDaoImpl(JdbcTemplate jdbcTemplate)
    {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional(readOnly = true)
    @Override
    public List<ScheduleTemplate> findAll()
    {
        final String query = "SELECT id, name, start_date, week_count, is_active FROM schedule_template";
        return jdbcTemplate.query(query, new ScheduleTemplateRowMapper());
    }

    @Transactional(readOnly = true)
    @Override
    public ScheduleTemplate findById(long id)
    {
        final String query = "SELECT id, name, start_date, week_count, is_active FROM schedule_template WHERE id = ?";
        try
        {
            return jdbcTemplate.queryForObject(query, new ScheduleTemplateRowMapper(), id);
        }
        catch (EmptyResultDataAccessException e)
        {
            return null;
        }
    }

    @Transactional(readOnly = true)
    @Override
    public ScheduleTemplate findByName(String name)
    {
        final String query = "SELECT id, name, start_date, week_count, is_active FROM schedule_template WHERE name = ?";
        try
        {
            return jdbcTemplate.queryForObject(query, new ScheduleTemplateRowMapper(), name);
        }
        catch (EmptyResultDataAccessException e)
        {
            return null;
        }
    }

    @Transactional(readOnly = true)
    @Override
    public List<ScheduleTemplate> findByIsActive(boolean isActive)
    {
        final String query = "SELECT id, name, start_date, week_count, is_active FROM schedule_template WHERE is_active = ?";
        try
        {
            return jdbcTemplate.query(query, new ScheduleTemplateRowMapper(), isActive);
        }
        catch (EmptyResultDataAccessException e)
        {
            return null;
        }
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    @Override
    public ScheduleTemplate save(ScheduleTemplate templateSchedule)
    {
        if (templateSchedule == null)
            throw new NullPointerException("ScheduleTemplate cannot be null");
        else if (templateSchedule.getName() == null || templateSchedule.getName().isEmpty())
            throw new NullPointerException("ScheduleTemplate name cannot be null or empty");
        else if (templateSchedule.getWeekCount() <= 0)
            throw new NullPointerException("ScheduleTemplate weekCount cannot lower 0");
        else if (templateSchedule.getStartDate() == null)
            throw new NullPointerException("ScheduleTemplate startDate cannot be null or empty");
        if (templateSchedule.getId() != 0 && findById(templateSchedule.getId()) != null)
        {
            final String query = """
                    UPDATE schedule_template SET name = ?, start_date = ?, week_count = ?, is_active = ? WHERE id = ?;
                    """;
            jdbcTemplate.update(query, templateSchedule.getName(), templateSchedule.getStartDate(), templateSchedule.getWeekCount(), templateSchedule.isActive(), templateSchedule.getId());
            return findById(templateSchedule.getId());
        }
        final String query = """
                INSERT INTO schedule_template (name, start_date, week_count, is_active) VALUES(?, ?, ?, ?);
                """;
        KeyHolder keyHolder = new GeneratedKeyHolder();
        PreparedStatementCreator preparedStatementCreator = new PreparedStatementCreator()
        {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException
            {
                PreparedStatement preparedStatement = con.prepareStatement(query, new String[] {"id"});
                preparedStatement.setString(1, templateSchedule.getName());
                preparedStatement.setDate(2, Date.valueOf(templateSchedule.getStartDate()));
                preparedStatement.setInt(3, templateSchedule.getWeekCount());
                preparedStatement.setBoolean(4, templateSchedule.isActive());
                return preparedStatement;
            }
        };
        jdbcTemplate.update(preparedStatementCreator, keyHolder);
        return findById(Objects.requireNonNull(keyHolder.getKey()).longValue());
    }

    @Transactional
    @Override
    public void deleteById(long id)
    {
        final String query  = "DELETE FROM schedule_template WHERE id = ?";
        jdbcTemplate.update(query, id);
    }


    private static class ScheduleTemplateRowMapper implements RowMapper<ScheduleTemplate>
    {
        @Override
        public ScheduleTemplate mapRow(ResultSet rs, int rowNum) throws SQLException
        {
            ScheduleTemplate templateSchedule = new ScheduleTemplate();
            templateSchedule.setId(rs.getLong("id"));
            templateSchedule.setName(rs.getString("name"));
            templateSchedule.setWeekCount(rs.getInt("week_count"));
            templateSchedule.setStartDate(rs.getDate("start_date").toLocalDate());
            templateSchedule.setActive(rs.getBoolean("is_active"));
            return templateSchedule;
        }
    }
}
