package org.letunov.service;

import org.letunov.service.dto.ScheduleDto;
import org.springframework.http.ResponseEntity;

public interface ScheduleService
{
    ResponseEntity<ScheduleDto> getGroupSchedule(int weekNumber, String groupName);
    ResponseEntity<String> updateSchedule(ScheduleDto scheduleDto);
}
