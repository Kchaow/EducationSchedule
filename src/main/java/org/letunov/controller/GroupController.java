package org.letunov.controller;

import org.letunov.domainModel.Group;
import org.letunov.service.GroupService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/group")
public class GroupController
{
    final private GroupService groupService;
    public GroupController(GroupService groupService)
    {
        this.groupService = groupService;
    }

    @RequestMapping("/student/{studentId}")
    public ResponseEntity<Group> getStudentGroup(@PathVariable("studentId") long studentId)
    {
        return  groupService.getStudentGroup(studentId);
    }
}
