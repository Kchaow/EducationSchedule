package org.letunov.service;

import org.letunov.domainModel.Group;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface GroupService
{
    public List<String> getGroupsNames();
    public List<Group> getGroups();
    public ResponseEntity<Group> getStudentGroup(long studentId);
    public Group getGroupByName(String name);
}
