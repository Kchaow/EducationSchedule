package org.letunov.dao;

import org.letunov.domainModel.User;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Repository;

public interface UserDao
{
    User findById(long id);
    Page<User> findByRole(String role);
    Page<User> findByFirstNameOrderByFirstName(String firstName);
    Page<User> findByLastNameOrderByLastName(String lastName);
    Page<User> findByMiddleNameOrderByMiddleName(String middleName);
    User findByLogin(String login);
    User findByEmail(String email);
    void deleteById(long id);
    User save(User user);
}
