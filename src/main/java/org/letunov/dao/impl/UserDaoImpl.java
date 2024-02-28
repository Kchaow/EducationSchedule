package org.letunov.dao.impl;

import org.letunov.dao.GroupDao;
import org.letunov.dao.RoleDao;
import org.letunov.dao.UserDao;
import org.letunov.domainModel.Group;
import org.letunov.domainModel.Role;
import org.letunov.domainModel.User;
import org.letunov.exceptions.TheDependentEntityIsPreservedBeforeTheIndependentEntity;
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

public class UserDaoImpl implements UserDao
{
    private final JdbcTemplate jdbcTemplate;
    private final GroupDao groupDao;
    private final RoleDao roleDao;

    public UserDaoImpl(JdbcTemplate jdbcTemplate, GroupDao groupDao, RoleDao roleDao)
    {
        this.jdbcTemplate = jdbcTemplate;
        this.groupDao = groupDao;
        this.roleDao = roleDao;
    }

    @Override
    public User findById(long id)
    {
        final String query = """
                SELECT u.id user_id, u.first_name, u.last_name, u.email, u.middle_name, u.password, u.login, u.role_id, u.group_id, r.name role_name, gr.name group_name
                FROM "user" u
                LEFT JOIN "group" gr ON u.group_id = gr.id
                LEFT JOIN role r ON u.role_id = r.id
                WHERE u.id = ?;
                """;
        try
        {
            return jdbcTemplate.queryForObject(query, new UserRowMapper(), id);
        }
        catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public Page<User> findByRole(String role, int limit, int offset)
    {
        final String query = """
                SELECT u.id user_id, u.first_name, u.last_name, u.email, u.middle_name, u.password, u.login, u.role_id, u.group_id, r.name role_name, gr.name group_name
                FROM "user" u
                LEFT JOIN "group" gr ON u.group_id = gr.id
                LEFT JOIN role r ON u.role_id = r.id
                WHERE r.name = ?
                LIMIT ? OFFSET ?;
                """;
        try
        {
            return new PageImpl<>(jdbcTemplate.query(query, new UserRowMapper(), role, limit, offset));
        }
        catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public Page<User> findByFirstNameOrderByFirstName(String firstName,int limit,int offset)
    {
        final String query = """
                SELECT u.id user_id, u.first_name, u.last_name, u.email, u.middle_name, u.password, u.login, u.role_id, u.group_id, r.name role_name, gr.name group_name
                FROM "user" u
                LEFT JOIN "group" gr ON u.group_id = gr.id
                LEFT JOIN role r ON u.role_id = r.id
                WHERE u.first_name = ?
                ORDER BY u.first_name
                LIMIT ? OFFSET ?;
                """;
        try
        {
            return new PageImpl<>(jdbcTemplate.query(query, new UserRowMapper(), firstName, limit, offset));
        }
        catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public Page<User> findByLastNameOrderByLastName(String lastName, int limit, int offset)
    {
        final String query = """
                SELECT u.id user_id, u.first_name, u.last_name, u.email, u.middle_name, u.password, u.login, u.role_id, u.group_id, r.name role_name, gr.name group_name
                FROM "user" u
                LEFT JOIN "group" gr ON u.group_id = gr.id
                LEFT JOIN role r ON u.role_id = r.id
                WHERE u.last_name = ?
                ORDER BY u.last_name
                LIMIT ? OFFSET ?;
                """;
        try
        {
            return new PageImpl<>(jdbcTemplate.query(query, new UserRowMapper(), lastName, limit, offset));
        }
        catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public Page<User> findByMiddleNameOrderByMiddleName(String middleName, int limit, int offset)
    {
        final String query = """
                SELECT u.id user_id, u.first_name, u.last_name, u.email, u.middle_name, u.password, u.login, u.role_id, u.group_id, r.name role_name, gr.name group_name
                FROM "user" u
                LEFT JOIN "group" gr ON u.group_id = gr.id
                LEFT JOIN role r ON u.role_id = r.id
                WHERE u.middle_name = ?
                ORDER BY u.middle_name
                LIMIT ? OFFSET ?;
                """;
        try
        {
            return new PageImpl<>(jdbcTemplate.query(query, new UserRowMapper(), middleName, limit, offset));
        }
        catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public User findByLogin(String login)
    {
        final String query = """
                SELECT u.id user_id, u.first_name, u.last_name, u.email, u.middle_name, u.password, u.login, u.role_id, u.group_id, r.name role_name, gr.name group_name
                FROM "user" u
                LEFT JOIN "group" gr ON u.group_id = gr.id
                LEFT JOIN role r ON u.role_id = r.id
                WHERE u.login = ?;
                """;
        try
        {
            return jdbcTemplate.queryForObject(query, new UserRowMapper(), login);
        }
        catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public User findByEmail(String email)
    {
        final String query = """
                SELECT u.id user_id, u.first_name, u.last_name, u.email, u.middle_name, u.password, u.login, u.role_id, u.group_id, r.name role_name, gr.name group_name
                FROM "user" u
                LEFT JOIN "group" gr ON u.group_id = gr.id
                LEFT JOIN role r ON u.role_id = r.id
                WHERE u.email = ?;
                """;
        try
        {
            return jdbcTemplate.queryForObject(query, new UserRowMapper(), email);
        }
        catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public void deleteById(long id)
    {
        final String query = "DELETE FROM \"user\" WHERE id = ?";
        jdbcTemplate.update(query, id);
    }

    @Override
    public User save(User user)
    {
        if (user.getGroup() != null && groupDao.findById(user.getGroup().getId()) == null)
            throw new TheDependentEntityIsPreservedBeforeTheIndependentEntity("Trying to save a dependent user entity before an independent group entity");
        if (user.getRole() != null && roleDao.findById(user.getRole().getId()) == null)
            throw new TheDependentEntityIsPreservedBeforeTheIndependentEntity("Trying to save a dependent user entity before an independent group entity");

        if (user.getId() != 0 && findById(user.getId()) != null)
        {
            final String query = """
                    UPDATE "user" SET first_name = ?, last_name = ?, middle_name = ?, email = ?,
                                      login = ?, password = ?, group_id = ?, role_id = ? WHERE id = ?;
                    """;
            if (user.getGroup() == null)
                jdbcTemplate.update(query, user.getFirstName(), user.getLastName(), user.getMiddleName(), user.getEmail(),
                    user.getLogin(), user.getPassword(), null, user.getRole().getId(), user.getId());
            else
                jdbcTemplate.update(query, user.getFirstName(), user.getLastName(), user.getMiddleName(), user.getEmail(),
                        user.getLogin(), user.getPassword(), user.getGroup().getId(), user.getRole().getId(), user.getId());
            return findById(user.getId());
        }
        final String query = """
                INSERT INTO "user"(first_name, last_name, middle_name, email, login, password, group_id, role_id)
                VALUES(?,?,?,?,?,?,?,?);
                """;
        KeyHolder keyHolder = new GeneratedKeyHolder();
        PreparedStatementCreator preparedStatementCreator = new PreparedStatementCreator()
        {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException
            {
                PreparedStatement preparedStatement = con.prepareStatement(query, new String[] {"id"});
                preparedStatement.setString(1, user.getFirstName());
                preparedStatement.setString(2, user.getLastName());
                if (user.getMiddleName() != null)
                    preparedStatement.setString(3, user.getMiddleName());
                else
                    preparedStatement.setNull(3, Types.NULL);
                preparedStatement.setString(4, user.getEmail());
                preparedStatement.setString(5, user.getLogin());
                preparedStatement.setString(6, user.getPassword());
                if (user.getGroup() != null)
                    preparedStatement.setLong(7, user.getGroup().getId());
                else
                    preparedStatement.setNull(7, Types.NULL);
                preparedStatement.setLong(8, user.getRole().getId());
                return preparedStatement;
            }
        };
        jdbcTemplate.update(preparedStatementCreator, keyHolder);
        return findById(Objects.requireNonNull(keyHolder.getKey()).longValue());
    }

    @Override
    public Iterable<User> saveAll(Iterable<User> users) {
        return null;
    }

    private static class UserRowMapper implements RowMapper<User>
    {
        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException
        {
            User user = new User();
            user.setId(rs.getLong("user_id"));
            user.setFirstName(rs.getString("first_name"));
            user.setLastName(rs.getString("last_name"));
            user.setMiddleName(rs.getString("middle_name"));
            user.setEmail(rs.getString("email"));
            user.setLogin(rs.getString("login"));
            user.setPassword(rs.getString("password"));
            Role role = new Role();
            role.setId(rs.getLong("role_id"));
            role.setName(rs.getString("role_name"));
            user.setRole(role);
            Group group = new Group();
            group.setId(rs.getLong("group_id"));
            group.setName(rs.getString("group_name"));
            user.setGroup(group);
            return user;
        }
    }
}
