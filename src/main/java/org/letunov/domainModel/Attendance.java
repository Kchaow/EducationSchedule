package org.letunov.domainModel;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class Attendance extends AbstractEntity
{
    private AttendanceStatus attendanceStatus;
    private User user;
    private Class clazz;
}
