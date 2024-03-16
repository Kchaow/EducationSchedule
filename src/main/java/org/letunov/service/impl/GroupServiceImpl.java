package org.letunov.service.impl;

import org.letunov.dao.GroupDao;
import org.letunov.domainModel.Group;
import org.letunov.service.GroupService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class GroupServiceImpl implements GroupService
{
    private GroupDao groupDao;
    public GroupServiceImpl(GroupDao groupDao)
    {
        this.groupDao = groupDao;
    }

    @Override //стримы?
    public List<String> getGroupsNames()
    {
        List<String> groupNames = new ArrayList<>();
        groupDao.findAllOrderByNameAsc(10, 0).stream().forEach((group) -> groupNames.add(group.getName()));
        return groupNames;
    }
}
