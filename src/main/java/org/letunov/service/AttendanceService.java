package org.letunov.service;

import org.letunov.service.dto.AttendanceDto;
import org.springframework.http.ResponseEntity;

public interface AttendanceService
{
    public ResponseEntity<AttendanceDto> getAttendance(long studentId, long educationDayId);
}
