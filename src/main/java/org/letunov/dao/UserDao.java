package org.letunov.dao;

import org.letunov.domainModel.User;
import org.springframework.data.domain.Page;

public interface UserDao
{
    User findById(long id);
    Page<User> findByRole(String role, int limit, int offset);
    Page<User> findByFirstNameOrderByFirstName(String firstName, int limit, int offset);
    Page<User> findByLastNameOrderByLastName(String lastName, int limit, int offset);
    Page<User> findByMiddleNameOrderByMiddleName(String middleName, int limit, int offset);
    User findByLogin(String login);
    User findByEmail(String email);
    void deleteById(long id);
    User save(User user);
    Iterable<User> saveAll(Iterable<User> users);
}
