package org.letunov.dao.impl;

import lombok.Getter;
import lombok.Setter;
import org.letunov.dao.SubjectDao;
import org.letunov.domainModel.Subject;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

@Repository
@Transactional
public class SubjectDaoImpl implements SubjectDao
{
    private final JdbcTemplate jdbcTemplate;
    public SubjectDaoImpl(JdbcTemplate jdbcTemplate)
    {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    @Transactional(readOnly = true)
    public Subject findById(long id)
    {
        final String query = "SELECT id, name FROM subject WHERE id = ?;";
        try
        {
            return jdbcTemplate.queryForObject(query, new SubjectRowMapper(), id);
        }
        catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Subject> findAll(int limit, int offset)
    {
        final String query = "SELECT id, name FROM subject LIMIT ? OFFSET ?";
        return new PageImpl<>(jdbcTemplate.query(query, new SubjectRowMapper(), limit, offset));
    }

    @Override
    @Transactional(readOnly = true)
    public Subject findByName(String name)
    {
        if (name == null)
            throw new NullPointerException("name cannot be null");
        final String query = "SELECT id, name FROM subject WHERE name = ?";
        try
        {
            return jdbcTemplate.queryForObject(query, new SubjectRowMapper(), name);
        }
        catch (EmptyResultDataAccessException e)
        {
            return null;
        }
    }

    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public Subject save(Subject subject)
    {
        if (subject == null)
            throw new NullPointerException("subject cannot be null");
        else if (subject.getName() == null)
            throw new NullPointerException("subject name cannot be null");
        if (subject.getId() != 0 && findById(subject.getId()) != null)
        {
            final String query = "UPDATE subject SET name = ? WHERE id = ?;";
            jdbcTemplate.update(query, subject.getName(), subject.getId());
            return findById(subject.getId());
        }
        final String query = "INSERT INTO subject(name) VALUES(?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        SubjectPreparedStatementCreator preparedStatementCreator = new SubjectPreparedStatementCreator();
        preparedStatementCreator.setQuery(query);
        preparedStatementCreator.setSubject(subject);
        jdbcTemplate.update(preparedStatementCreator, keyHolder);
        return findById(Objects.requireNonNull(keyHolder.getKey()).longValue());
    }

    @Override
    @Transactional
    public void deleteById(long id)
    {
        final String query = "DELETE FROM subject WHERE id = ?";
        jdbcTemplate.update(query, id);
    }

    private static class SubjectRowMapper implements RowMapper<Subject>
    {
        @Override
        public Subject mapRow(ResultSet rs, int rowNum) throws SQLException
        {
            Subject subject = new Subject();
            subject.setName(rs.getString("name"));
            subject.setId(rs.getLong("id"));
            return subject;
        }
    }

    @Setter
    @Getter
    private  static class SubjectPreparedStatementCreator implements PreparedStatementCreator
    {
        private String query;
        private Subject subject;
        @Override
        public PreparedStatement createPreparedStatement(Connection con) throws SQLException
        {
            PreparedStatement preparedStatement = con.prepareStatement(query, new String[] {"id"});
            preparedStatement.setString(1, subject.getName());
            return preparedStatement;
        }
    }
}
