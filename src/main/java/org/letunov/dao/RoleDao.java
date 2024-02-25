package org.letunov.dao;

import org.letunov.domainModel.Role;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface RoleDao
{
    List<Role> findAll();
    Role findById(long id);
}
