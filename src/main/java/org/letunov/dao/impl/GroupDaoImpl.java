package org.letunov.dao.impl;

import org.letunov.dao.GroupDao;
import org.letunov.dao.UserDao;
import org.letunov.domainModel.Group;
import org.letunov.domainModel.Role;
import org.letunov.domainModel.User;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class GroupDaoImpl implements GroupDao
{
    private final JdbcTemplate jdbcTemplate;
    private final UserDao userDao;

    public GroupDaoImpl(JdbcTemplate jdbcTemplate, UserDao userDao)
    {
        this.jdbcTemplate = jdbcTemplate;
        this.userDao = userDao;
    }
    @Override
    public Page<Group> findAllOrderByNameAsc(int limit, int offset)
    {
        final String query = """
                SELECT gr.id group_id, gr.name AS group_name, u.id AS user_id, u.first_name, u.last_name,
                       u.middle_name, u.password, u.login, u.email, r.id role_id, r.name AS role_name
                       FROM "group" gr
                       LEFT JOIN "user" u ON gr.id = u.group_id
                       LEFT JOIN role r ON r.id = u.role_id
                       ORDER BY gr.name ASC
                       LIMIT ? OFFSET ?;
                """;
        Object[] args = new Object[] {limit, offset};
        int[] argTypes = new int[] {Types.INTEGER, Types.INTEGER};
        ResultSetExtractor<Page<Group>> GroupWithUserPageExtractor = new GroupWithUserPageExtractor();
        return jdbcTemplate.query(query, args, argTypes, GroupWithUserPageExtractor);
    }

    @Override
    public Group findById(long id)
    {
        final String query = """
                SELECT gr.id group_id, gr.name AS group_name, u.id AS user_id, u.first_name, u.last_name,
                       u.middle_name, u.password, u.login, u.email, r.id role_id, r.name AS role_name
                       FROM "group" gr
                       LEFT JOIN "user" u ON gr.id = u.group_id
                       LEFT JOIN role r ON r.id = u.role_id
                       WHERE gr.id = ?;
                """;
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(query, id);
        return getGroupMap(rowSet).get(id);
    }

    @Override
    public Group findByName(String name)
    {
        final String query = """
                SELECT gr.id group_id, gr.name AS group_name, u.id AS user_id, u.first_name, u.last_name,
                       u.middle_name, u.password, u.login, u.email, r.id role_id, r.name AS role_name
                       FROM "group" gr
                       LEFT JOIN "user" u ON gr.id = u.group_id
                       LEFT JOIN role r ON r.id = u.role_id
                       WHERE gr.name = ?;
                """;
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(query, name);
        for (Map.Entry<Long, Group> entry : getGroupMap(rowSet).entrySet())
            return entry.getValue();
        return null;
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
        if (group.getUser() != null && !group.getUser().isEmpty())
        {
            userDao.saveAll(group.getUser()); //delegate saving and updating
            return findByName(group.getName());
        }
        String query = "";
        if (group.getId() != 0 && findById(group.getId()) != null)
        {
            query = "UPDATE \"group\" SET name=?";
            jdbcTemplate.update(query, group.getName());
            return findById(group.getId());
        }
        else
        {
            query = "INSERT INTO \"group\"(name) VALUES(?)";
            jdbcTemplate.update(query, group.getName()); //PreparedStatement для взятия ключа
            return  findByName(group.getName());
        }
    }

    private static class GroupWithUserPageExtractor implements ResultSetExtractor<Page<Group>>
    {

        @Override
        public Page<Group> extractData(ResultSet rs) throws SQLException
        {
            return new PageImpl<Group>(getGroupMap(rs).values().stream().toList());
        }
    }

    private static Map<Long, Group> getGroupMap(ResultSet rs) throws SQLException
    {
        Map<Long, Group> map = new HashMap<>();
        Group group;
        while (rs.next())
        {
            long id = rs.getLong("group_id");
            group = map.get(id);
            if(group == null)
            {
                group = new Group();
                group.setId(id);
                group.setName(rs.getString("group_name"));
                group.setUser(new HashSet<>());
                map.put(id, group);
            }

            long userId = rs.getLong("user_id");
            if (userId != 0)
            {
                User user = new User();
                user.setId(userId);
                user.setFirstName(rs.getString("first_name"));
                user.setLastName(rs.getString("last_name"));
                user.setMiddleName(rs.getString("middle_name"));
                user.setEmail(rs.getString("email"));
                user.setLogin(rs.getString("login"));
                user.setGroup(group);
                group.getUser().add(user);

                long roleId = rs.getLong("role_id");
                if (roleId != 0)
                {
                    Role role = new Role();
                    role.setId(roleId);
                    role.setName(rs.getString("role_name"));
                    user.setRole(role);
                }
            }
        }
        return map;
    }

    private static Map<Long, Group> getGroupMap(SqlRowSet rs)
    {
        Map<Long, Group> map = new HashMap<>();
        Group group;
        while (rs.next())
        {
            long id = rs.getLong("group_id");
            group = map.get(id);
            if(group == null)
            {
                group = new Group();
                group.setId(id);
                group.setName(rs.getString("group_name"));
                group.setUser(new HashSet<>());
                map.put(id, group);
            }

            long userId = rs.getLong("user_id");
            if (userId != 0)
            {
                User user = new User();
                user.setId(userId);
                user.setFirstName(rs.getString("first_name"));
                user.setLastName(rs.getString("last_name"));
                user.setMiddleName(rs.getString("middle_name"));
                user.setEmail(rs.getString("email"));
                user.setLogin(rs.getString("login"));
                user.setGroup(group);
                group.getUser().add(user);

                long roleId = rs.getLong("role_id");
                if (roleId != 0)
                {
                    Role role = new Role();
                    role.setId(roleId);
                    role.setName(rs.getString("role_name"));
                    user.setRole(role);
                }
            }
        }
        return map;
    }
}
