package org.letunov.service.impl;

import org.letunov.dao.RoleDao;
import org.letunov.domainModel.Role;
import org.letunov.service.RoleService;
import org.springframework.stereotype.Service;

@Service
public class RoleServiceIml implements RoleService
{
    private final RoleDao roleDao;

    public RoleServiceIml(RoleDao roleDao)
    {
        this.roleDao = roleDao;
    }

    @Override
    public Role getRoleByName(String name)
    {
        return roleDao.findByName(name);
    }
}
