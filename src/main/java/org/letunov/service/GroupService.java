package org.letunov.service;

import org.letunov.domainModel.Group;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface GroupService
{
    List<String> getGroupsNames();

    ResponseEntity<Group> getStudentGroup(long studentId);
}
