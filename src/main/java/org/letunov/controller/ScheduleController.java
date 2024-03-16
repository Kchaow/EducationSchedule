package org.letunov.controller;

import org.letunov.service.ScheduleService;
import org.letunov.service.dto.ScheduleDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
@RequestMapping("/schedule")
public class ScheduleController
{
    private final ScheduleService scheduleService;
    public ScheduleController(ScheduleService scheduleService)
    {
        this.scheduleService = scheduleService;
    }

    @GetMapping("/{groupName}/{weekNumber}")
    public ResponseEntity<ScheduleDto> getSchedule(@PathVariable("groupName") String groupName,@PathVariable("weekNumber") int weekNumber)
    {
        return scheduleService.getGroupSchedule(weekNumber, groupName);
    }

    @GetMapping
    public String getScheduleModel(Model model)
    {

        return "schedule";
    }
}
