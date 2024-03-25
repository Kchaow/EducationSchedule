package org.letunov.dao;

import org.letunov.domainModel.User;
import org.springframework.data.domain.Page;

import java.util.List;

public interface UserDao
{
    User findById(long id);
    List<User> findByRole(String role);
    List<User> findByFirstNameOrderByFirstName(String firstName);
    List<User> findByLastNameOrderByLastName(String lastName);
    List<User> findByMiddleNameOrderByMiddleName(String middleName);
    User findByLogin(String login);
    User findByEmail(String email);
    void deleteById(long id);
    User save(User user);
    List<User> saveAll(List<User> users);
}
