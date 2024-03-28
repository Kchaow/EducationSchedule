package org.letunov.dao.impl;

import org.letunov.dao.RoleDao;
import org.letunov.domainModel.AttendanceStatus;
import org.letunov.domainModel.Role;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
@Transactional
public class RoleDaoImpl implements RoleDao
{
    private final JdbcTemplate jdbcTemplate;

    public RoleDaoImpl(JdbcTemplate jdbcTemplate)
    {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Role> findAll()
    {
        final String query = "SELECT id, name FROM role";
        RowMapper<Role> rowMapper = new RoleDaoImpl.RoleRowMapper();
        return jdbcTemplate.query(query, rowMapper);
    }

    @Override
    @Transactional(readOnly = true)
    public Role findById(long id)
    {
        final String query = "SELECT id, name FROM role WHERE id = ?";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(query, id);
        Role role = new Role();
        if (rowSet.first())
        {
            role.setId(rowSet.getInt(1));
            role.setName(rowSet.getString(2));
            return role;
        }
        return null;
    }

    @Override
    public Role findByName(String name)
    {
        final String query = "SELECT id, name FROM role WHERE name = ?";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(query, name);
        Role role = new Role();
        if (rowSet.first())
        {
            role.setId(rowSet.getInt(1));
            role.setName(rowSet.getString(2));
            return role;
        }
        return null;
    }

    private static class RoleRowMapper implements RowMapper<Role>
    {
        @Override
        public Role mapRow(ResultSet rs, int rowNum) throws SQLException
        {
            Role role = new Role();
            role.setId(rs.getLong("id"));
            role.setName(rs.getString("name"));
            return role;
        }
    }
}
