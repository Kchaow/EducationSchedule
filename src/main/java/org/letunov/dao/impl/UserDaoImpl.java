package org.letunov.dao.impl;

import lombok.Getter;
import lombok.Setter;
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
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.*;
import java.util.*;

@Repository
@Transactional
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
    @Transactional(readOnly = true)
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
    @Transactional(readOnly = true)
    public Page<User> findByRole(String role, int limit, int offset)
    {
        if (role == null)
            throw new NullPointerException("role arg cannot be null");
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
    @Transactional(readOnly = true)
    public Page<User> findByFirstNameOrderByFirstName(String firstName,int limit,int offset)
    {
        if (firstName == null)
            throw new NullPointerException("firstName arg cannot be null");
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
    @Transactional(readOnly = true)
    public Page<User> findByLastNameOrderByLastName(String lastName, int limit, int offset)
    {
        if (lastName == null)
            throw new NullPointerException("lastName arg cannot be null");
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
    @Transactional(readOnly = true)
    public Page<User> findByMiddleNameOrderByMiddleName(String middleName, int limit, int offset)
    {
        if (middleName == null)
            throw new NullPointerException("middleName arg cannot be null");
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
    @Transactional(readOnly = true)
    public User findByLogin(String login)
    {
        if (login == null)
            throw new NullPointerException("login arg cannot be null");
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
    @Transactional(readOnly = true)
    public User findByEmail(String email)
    {
        if (email == null)
            throw new NullPointerException("email arg cannot be null");
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
    @Transactional
    public void deleteById(long id)
    {
        final String query = "DELETE FROM \"user\" WHERE id = ?";
        jdbcTemplate.update(query, id);
    }

    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public User save(User user)
    {
        if (user == null)
            throw new NullPointerException("user arg cannot be null");
        else if (user.getFirstName() == null)
            throw new NullPointerException("user firstName cannot be null");
        else if (user.getLastName() == null)
            throw new NullPointerException("user lastName cannot be null");
        else if (user.getEmail() == null)
            throw new NullPointerException("user email cannot be null");
        else if (user.getLogin() == null)
            throw new NullPointerException("user login cannot be null");
        else if (user.getPassword() == null)
            throw new NullPointerException("user password cannot be null");
        else if (user.getRole() == null)
            throw new NullPointerException("user role cannot be null");
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
        PreparedStatementSaveCreator preparedStatementSaveCreator = new PreparedStatementSaveCreator();
        preparedStatementSaveCreator.setUser(user);
        preparedStatementSaveCreator.setQuery(query);
        jdbcTemplate.update(preparedStatementSaveCreator, keyHolder);
        return findById(Objects.requireNonNull(keyHolder.getKey()).longValue());
    }

    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<User> saveAll(List<User> users)
    {
        if (users == null)
            throw new NullPointerException("users arg cannot be null");
        List<User> userResult = new ArrayList<>();
        for (int i = 0; i < users.size(); i++)
        {
            if (users.get(i) == null)
                throw new NullPointerException("user[%d] arg cannot be null".formatted(i));
            else if (users.get(i).getFirstName() == null)
                throw new NullPointerException("user[%d] firstName cannot be null".formatted(i));
            else if (users.get(i).getLastName() == null)
                throw new NullPointerException("user[%d] lastName cannot be null".formatted(i));
            else if (users.get(i).getEmail() == null)
                throw new NullPointerException("user[%d] email cannot be null".formatted(i));
            else if (users.get(i).getLogin() == null)
                throw new NullPointerException("user[%d] login cannot be null".formatted(i));
            else if (users.get(i).getPassword() == null)
                throw new NullPointerException("user[%d] password cannot be null".formatted(i));
            else if (users.get(i).getRole() == null)
                throw new NullPointerException("user[%d] role cannot be null".formatted(i));

            if (users.get(i).getGroup() != null && groupDao.findById(users.get(i).getGroup().getId()) == null)
                throw new TheDependentEntityIsPreservedBeforeTheIndependentEntity("Trying to save a dependent user entity before an independent group entity");
            if (users.get(i).getRole() != null && roleDao.findById(users.get(i).getRole().getId()) == null)
                throw new TheDependentEntityIsPreservedBeforeTheIndependentEntity("Trying to save a dependent user entity before an independent group entity");

            if (users.get(i).getId() != 0 && findById(users.get(i).getId()) != null)
            {
                final String query = """
                    UPDATE "user" SET first_name = ?, last_name = ?, middle_name = ?, email = ?,
                                      login = ?, password = ?, group_id = ?, role_id = ? WHERE id = ?;
                    """;
                if (users.get(i).getGroup() == null)
                    jdbcTemplate.update(query, users.get(i).getFirstName(), users.get(i).getLastName(), users.get(i).getMiddleName(), users.get(i).getEmail(),
                            users.get(i).getLogin(), users.get(i).getPassword(), null, users.get(i).getRole().getId(), users.get(i).getId());
                else
                    jdbcTemplate.update(query, users.get(i).getFirstName(), users.get(i).getLastName(), users.get(i).getMiddleName(), users.get(i).getEmail(),
                            users.get(i).getLogin(), users.get(i).getPassword(), users.get(i).getGroup().getId(), users.get(i).getRole().getId(), users.get(i).getId());
                userResult.add(findById(users.get(i).getId()));
            }
            else
            {
                final String query = """
                INSERT INTO "user"(first_name, last_name, middle_name, email, login, password, group_id, role_id)
                VALUES(?,?,?,?,?,?,?,?);
                """;
                KeyHolder keyHolder = new GeneratedKeyHolder();
                PreparedStatementSaveCreator preparedStatementSaveCreator = new PreparedStatementSaveCreator();
                preparedStatementSaveCreator.setUser(users.get(i));
                preparedStatementSaveCreator.setQuery(query);
                jdbcTemplate.update(preparedStatementSaveCreator, keyHolder);
                userResult.add(findById(Objects.requireNonNull(keyHolder.getKey()).longValue()));
            }
        }
        return userResult;
    }

    @Setter
    @Getter
    private static class PreparedStatementSaveCreator implements PreparedStatementCreator
    {
        private User user;
        private String query;
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
