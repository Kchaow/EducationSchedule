package org.letunov.service;

import org.letunov.service.dto.ClassDto;
import org.letunov.service.dto.ScheduleDto;
import org.springframework.http.ResponseEntity;

public interface ScheduleService
{
    ResponseEntity<ScheduleDto> getGroupSchedule(int weekNumber, String groupName);
    ResponseEntity<ClassDto> detachClass(long classId, long groupId);
    ResponseEntity<ScheduleDto> getGroupSchedule(int weekNumber, String groupName, long templateId);
    ResponseEntity<String> updateSchedule(ScheduleDto scheduleDto);
    ResponseEntity<ClassDto> saveOrUpdateClass(ClassDto classDto, long templateId);
    ResponseEntity<Object> deleteClass(long classId, long groupId);
}
