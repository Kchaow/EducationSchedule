package org.letunov.dao.impl;

import org.letunov.dao.UserDao;
import org.letunov.domainModel.User;
import org.springframework.data.domain.Page;
import org.springframework.jdbc.core.JdbcTemplate;

public class UserDaoImpl implements UserDao
{
    private final JdbcTemplate jdbcTemplate;

    public UserDaoImpl(JdbcTemplate jdbcTemplate)
    {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User findById(long id) {
        return null;
    }

    @Override
    public Page<User> findByRole(String role) {
        return null;
    }

    @Override
    public Page<User> findByFirstNameOrderByFirstName(String firstName) {
        return null;
    }

    @Override
    public Page<User> findByLastNameOrderByLastName(String lastName) {
        return null;
    }

    @Override
    public Page<User> findByMiddleNameOrderByMiddleName(String middleName) {
        return null;
    }

    @Override
    public User findByLogin(String login) {
        return null;
    }

    @Override
    public User findByEmail(String email) {
        return null;
    }

    @Override
    public void deleteById(long id) {

    }

    @Override
    public User save(User user) {
        return null;
    }

    @Override
    public Iterable<User> saveAll(Iterable<User> users) {
        return null;
    }
}
