package org.letunov.service.impl;


import org.letunov.dao.UserDao;
import org.letunov.domainModel.User;
import org.letunov.service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService
{
    final private UserDao userDao;

    public UserServiceImpl(UserDao userDao)
    {
        this.userDao = userDao;
    }

    public List<User> getTeachersList()
    {
        return userDao.findByRole("teacher");
    }
}
