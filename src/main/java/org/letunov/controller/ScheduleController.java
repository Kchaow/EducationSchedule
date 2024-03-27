package org.letunov.controller;

import org.letunov.domainModel.ScheduleTemplate;
import org.letunov.service.GroupService;
import org.letunov.service.ScheduleService;
import org.letunov.service.ScheduleTemplateService;
import org.letunov.service.dto.ClassDto;
import org.letunov.service.dto.ScheduleDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/schedule")
public class ScheduleController
{
    private final ScheduleService scheduleService;
    private final GroupService groupService;
    private final ScheduleTemplateService scheduleTemplateService;
    public ScheduleController(ScheduleService scheduleService, GroupService groupService, ScheduleTemplateService scheduleTemplateService)
    {
        this.scheduleService = scheduleService;
        this.groupService = groupService;
        this.scheduleTemplateService = scheduleTemplateService;
    }

    @CrossOrigin
    @GetMapping("/{groupName}/{weekNumber}")
    public ResponseEntity<ScheduleDto> getSchedule(@PathVariable("groupName") String groupName,@PathVariable("weekNumber") int weekNumber)
    {
        return scheduleService.getGroupSchedule(weekNumber, groupName);
    }

    @GetMapping("/activeTemplate")
    public ResponseEntity<ScheduleTemplate> getActiveTemplate()
    {
        return scheduleTemplateService.getActiveScheduleTemplate();
    }

    @CrossOrigin
    @GetMapping("/templates/{templateId}/{groupName}/{weekNumber}")
    public ResponseEntity<ScheduleDto> getSchedule(@PathVariable("groupName") String groupName,
                                                   @PathVariable("weekNumber") int weekNumber,
                                                   @PathVariable("templateId") long templateId)
    {
        return scheduleService.getGroupSchedule(weekNumber, groupName, templateId);
    }

    @PutMapping("/class/detach/{classId}/{groupId}")
    public ResponseEntity<ClassDto> detachClass(@PathVariable("classId") long classId,@PathVariable("groupId") long groupId)
    {
        return scheduleService.detachClass(classId, groupId);
    }

    @PutMapping("/templates/{templateId}")
    public ResponseEntity<ClassDto> saveOrUpdateClass(@RequestBody ClassDto classDto, @PathVariable("templateId") long templateId)
    {
        return scheduleService.saveOrUpdateClass(classDto, templateId);
    }

    @DeleteMapping("/class/{classId}/{groupId}")
    public ResponseEntity<Object> deleteClass(@PathVariable("classId") long classId,@PathVariable("groupId") long groupId)
    {
        return scheduleService.deleteClass(classId, groupId);
    }

    @GetMapping
    public String getScheduleModel(Model model)
    {
        List<String> groupNames = groupService.getGroupsNames();
        model.addAttribute("groups", groupNames);
        return "schedule";
    }
}
