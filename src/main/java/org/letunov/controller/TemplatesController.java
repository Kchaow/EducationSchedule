package org.letunov.controller;

import lombok.extern.slf4j.Slf4j;
import org.letunov.domainModel.ScheduleTemplate;
import org.letunov.domainModel.Subject;
import org.letunov.domainModel.User;
import org.letunov.service.GroupService;
import org.letunov.service.ScheduleTemplateService;
import org.letunov.service.SubjectService;
import org.letunov.service.UserService;
import org.letunov.util.LocalDateFormatter;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.format.datetime.DateFormatter;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/schedule/templates")
public class TemplatesController
{
    final ScheduleTemplateService scheduleTemplateService;
    final GroupService groupService;
    final SubjectService subjectService;
    final UserService userService;

    public TemplatesController(ScheduleTemplateService scheduleTemplateService, GroupService groupService, SubjectService subjectService, UserService userService)
    {
        this.scheduleTemplateService = scheduleTemplateService;
        this.groupService = groupService;
        this.subjectService = subjectService;
        this.userService = userService;
    }

    @InitBinder
    public void InitBinder(WebDataBinder webDataBinder)
    {
        webDataBinder.addCustomFormatter(new LocalDateFormatter());
    }

    @GetMapping
    public String getTemplates(Model model)
    {
        model.addAttribute("templates", scheduleTemplateService.getScheduleTemplates());
        model.addAttribute("newTemplate", new ScheduleTemplate());
        return "templates";
    }

    @PostMapping
    public String addNewTemplate(@ModelAttribute ScheduleTemplate newTemplate)
    {
        scheduleTemplateService.addNewScheduleTemplate(newTemplate);
        return "redirect: /EducationSchedule/schedule/templates";
    }

    @PostMapping("/update")
    public String updateTemplate(@ModelAttribute ScheduleTemplate template)
    {
        scheduleTemplateService.addNewScheduleTemplate(template);
        return "redirect: /EducationSchedule/schedule/templates/%d".formatted(template.getId());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteTemplate(@PathVariable("id") Long id)
    {
        scheduleTemplateService.deleteScheduleTemplate(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping
    public ResponseEntity<Object> makeTemplateActive(@RequestParam("templateName") String templateName)
    {
        scheduleTemplateService.makeTemplateActive(templateName);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public String getTemplate(@PathVariable("id") Long id, Model model)
    {
        List<String> groupNames = groupService.getGroupsNames();
        List<Subject> subjects = subjectService.getSubjectsList();
        List<User> teachers = userService.getTeachersList();
        ScheduleTemplate templateForModification = scheduleTemplateService.getScheduleTemplate(id);
        log.info(templateForModification.toString());
        model.addAttribute("subjects", subjects);
        model.addAttribute("groups", groupNames);
        model.addAttribute("teachers", teachers);
        model.addAttribute("modificatedTemplate", templateForModification);
        return "template";
    }
}
