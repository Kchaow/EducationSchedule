package org.letunov.dao.impl;

import org.letunov.dao.GroupDao;
import org.letunov.domainModel.Group;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.*;
import java.util.*;

public class GroupDaoImpl implements GroupDao
{
    private final JdbcTemplate jdbcTemplate;

    public GroupDaoImpl(JdbcTemplate jdbcTemplate)
    {
        this.jdbcTemplate = jdbcTemplate;
    }
    @Override
    public Page<Group> findAllOrderByNameAsc(int limit, int offset)
    {
        final String query = "SELECT id, name FROM \"group\" ORDER BY name ASC;";
        return new PageImpl<>(jdbcTemplate.query(query, new GroupRowMapper()));
    }

    @Override
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
    public void deleteById(long id)
    {
        final String query = "DELETE FROM \"group\" WHERE id = ?";
        jdbcTemplate.update(query, id);
    }

    //Transaction
    @Override
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
