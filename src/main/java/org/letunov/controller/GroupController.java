package org.letunov.controller;

import lombok.extern.slf4j.Slf4j;
import org.letunov.domainModel.Group;
import org.letunov.service.GroupService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping
@Slf4j
public class GroupController
{
    final private GroupService groupService;
    public GroupController(GroupService groupService)
    {
        this.groupService = groupService;
    }

    @RequestMapping("/group/student/{studentId}")
    public ResponseEntity<Group> getStudentGroup(@PathVariable("studentId") long studentId)
    {
        return  groupService.getStudentGroup(studentId);
    }

    @RequestMapping("/groups/manage")
    public String getGroupsPage(Model model)
    {
        List<Group> groups = groupService.getGroups();
        Group newGroup = new Group();
        model.addAttribute("groups", groups);
        model.addAttribute("newGroup", newGroup);
        return "groupManage";
    }

    @RequestMapping("/groups/{id}")
    public ResponseEntity<Object> deleteGroup(@PathVariable("id") long id)
    {
        return groupService.deleteGroup(id);
    }

    @PostMapping("/groups")
    public String createNewGroup(Group group)
    {
        groupService.createNewGroup(group);
        return "redirect: /EducationSchedule/groups/manage";
    }
}
