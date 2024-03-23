package org.letunov.service.impl;

import org.letunov.dao.AttendanceDao;
import org.letunov.domainModel.Attendance;
import org.letunov.service.AttendanceService;
import org.letunov.service.dto.AttendanceDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class AttendanceServiceImpl implements AttendanceService
{
    private final AttendanceDao attendanceDao;

    public AttendanceServiceImpl(AttendanceDao attendanceDao)
    {
        this.attendanceDao = attendanceDao;
    }

    @Override
    public ResponseEntity<AttendanceDto> getAttendance(long studentId, long classId)
    {
        Attendance attendance = attendanceDao.findByStudentIdAndClassId(studentId, classId).getFirst();
        AttendanceDto attendanceDto = AttendanceDto.builder()
                .id(attendance.getId())
                .attendanceStatus(attendance.getAttendanceStatus() == null ? null : attendance.getAttendanceStatus().getName())
                .build();
        return new ResponseEntity<AttendanceDto>(attendanceDto, HttpStatus.OK);
    }
}
