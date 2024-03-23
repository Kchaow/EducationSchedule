package org.letunov.domainModel;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = true)
public class Class extends AbstractEntity
{
    private int weekNumber;
    private User user;
    private List<Group> group;
    private Subject subject;
    private DayOfWeek dayOfWeek;
    private int classNumber;
    private int audience;
    private ScheduleTemplate scheduleTemplate;
}
