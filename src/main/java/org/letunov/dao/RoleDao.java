package org.letunov.dao;

import org.letunov.domainModel.Role;

import java.util.List;

public interface RoleDao
{
    List<Role> findAll();
    Role findById(long id);
    Role findByName(String name);
}
