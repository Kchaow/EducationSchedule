package org.letunov.controller;

import lombok.extern.slf4j.Slf4j;
import org.letunov.domainModel.ScheduleTemplate;
import org.letunov.service.ScheduleTemplateService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Controller
@RequestMapping("/schedule/templates")
public class TemplatesController
{
    final ScheduleTemplateService scheduleTemplateService;

    public TemplatesController(ScheduleTemplateService scheduleTemplateService)
    {
        this.scheduleTemplateService = scheduleTemplateService;
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

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteTemplate(@PathVariable("id") Long id)
    {
        scheduleTemplateService.deleteScheduleTemplate(id);
        return ResponseEntity.noContent().build();
    }
}
