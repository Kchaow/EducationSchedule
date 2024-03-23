package org.letunov.dao.impl;

import org.letunov.dao.GroupDao;
import org.letunov.domainModel.Group;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.*;
import java.util.*;

@Repository
@Transactional
public class GroupDaoImpl implements GroupDao
{
    private final JdbcTemplate jdbcTemplate;

    public GroupDaoImpl(JdbcTemplate jdbcTemplate)
    {
        this.jdbcTemplate = jdbcTemplate;
    }
    @Override
    public Page<Group> findAllOrderByNameAsc(int size, int page)
    {
        Pageable pageable = PageRequest.of(page, size);
        final String query = "SELECT id, name FROM \"group\" ORDER BY name ASC;";
        List<Group> groups = jdbcTemplate.query(query, new GroupRowMapper());
        return new PageImpl<>(groups, pageable, groups.size());
    }

    @Override
    public Group findByUserId(long id)
    {
        final String query = "SELECT gr.id, name FROM \"group\" gr JOIN \"user\" u ON gr.id = u.group_id WHERE u.id=?";
        try
        {
            return jdbcTemplate.queryForObject(query, new GroupRowMapper(), id);
        }
        catch (EmptyResultDataAccessException e)
        {
            return null;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Group findById(long id)
    {
        final String query = "SELECT id, name FROM \"group\" WHERE id = ?";
        try
        {
            return jdbcTemplate.queryForObject(query, new GroupRowMapper(), id);
        }
        catch (EmptyResultDataAccessException e)
        {
            return null;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Group findByName(String name)
    {
        if (name == null)
            throw new NullPointerException("name arg cannot be null");
        final String query = "SELECT id, name FROM \"group\" WHERE name = ?";
        try
        {
            return jdbcTemplate.queryForObject(query, new GroupRowMapper(), name);
        }
        catch (EmptyResultDataAccessException e)
        {
            return null;
        }
    }

    @Override
    @Transactional
    public void deleteById(long id)
    {
        final String query = "DELETE FROM \"group\" WHERE id = ?";
        jdbcTemplate.update(query, id);
    }

    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public Group save(Group group)
    {
        if (group == null)
            throw new NullPointerException("group cannot be null");
        else if (group.getName() == null)
            throw new NullPointerException("group name cannot be null");
        if (group.getId() != 0 && findById(group.getId()) != null)
        {
            final String query = """
                    UPDATE "group" SET name = ? WHERE id = ?;
                    """;
            jdbcTemplate.update(query, group.getName(), group.getId());
            return findById(group.getId());
        }
        final String query = """
                INSERT INTO "group"(name) VALUES(?);
                """;
        KeyHolder keyHolder = new GeneratedKeyHolder();
        PreparedStatementCreator preparedStatementCreator = new PreparedStatementCreator()
        {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException
            {
                PreparedStatement preparedStatement = con.prepareStatement(query, new String[] {"id"});
                preparedStatement.setString(1, group.getName());
                return preparedStatement;
            }
        };
        jdbcTemplate.update(preparedStatementCreator, keyHolder);
        return findById(Objects.requireNonNull(keyHolder.getKey()).longValue());
    }

    private static class GroupRowMapper implements RowMapper<Group>
    {
        @Override
        public Group mapRow(ResultSet rs, int rowNum) throws SQLException
        {
            Group group = new Group();
            group.setId(rs.getLong("id"));
            group.setName(rs.getString("name"));
            return group;
        }
    }
}
