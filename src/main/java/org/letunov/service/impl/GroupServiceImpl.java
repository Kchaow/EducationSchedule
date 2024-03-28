package org.letunov.service.impl;

import org.letunov.dao.GroupDao;
import org.letunov.domainModel.Group;
import org.letunov.service.GroupService;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class GroupServiceImpl implements GroupService
{
    private GroupDao groupDao;
    public GroupServiceImpl(GroupDao groupDao)
    {
        this.groupDao = groupDao;
    }

    @Override
    public List<String> getGroupsNames()
    {
        List<String> groupNames = new ArrayList<>();
        groupDao.findAllOrderByNameAsc().forEach((group) -> groupNames.add(group.getName()));
        return groupNames;
    }

    @Override
    public List<Group> getGroups()
    {
        return groupDao.findAllOrderByNameAsc();
    }

    @Override
    public ResponseEntity<Group> getStudentGroup(long studentId)
    {
        Group group = groupDao.findByUserId(studentId);
        if (group == null)
            throw new NoSuchElementException("user %d doesn't have group".formatted(studentId));
        return new ResponseEntity<Group>(group, HttpStatus.OK);
    }

    @Override
    public Group getGroupByName(String name)
    {
        return groupDao.findByName(name);
    }

    @Override
    public Group createNewGroup(Group group) {
        return groupDao.save(group);
    }
}
