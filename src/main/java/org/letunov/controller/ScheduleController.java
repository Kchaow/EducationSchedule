package org.letunov.controller;

import org.letunov.service.ScheduleService;
import org.letunov.service.dto.ScheduleDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
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
}
