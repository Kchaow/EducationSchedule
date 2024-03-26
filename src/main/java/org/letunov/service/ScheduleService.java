package org.letunov.service;

import org.letunov.service.dto.ClassDto;
import org.letunov.service.dto.ScheduleDto;
import org.springframework.http.ResponseEntity;

public interface ScheduleService
{
    ResponseEntity<ScheduleDto> getGroupSchedule(int weekNumber, String groupName);
    ResponseEntity<ScheduleDto> getGroupSchedule(int weekNumber, String groupName, long templateId);
    ResponseEntity<String> updateSchedule(ScheduleDto scheduleDto);
    ResponseEntity<Long> saveOrUpdateClass(ClassDto classDto, long templateId);
}
