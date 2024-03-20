package org.letunov.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.letunov.domainModel.AttendanceStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AttendanceDto
{
    private long id;
    private String attendanceStatus;
}
