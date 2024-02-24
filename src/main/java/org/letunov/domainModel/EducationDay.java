package org.letunov.domainModel;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.DayOfWeek;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
public class EducationDay extends AbstractEntity
{
    private int weekNumber;
    private User user;
    private Group group;
    private DayOfWeek dayOfWeek;
    private Subject subject;
    private LocalDate date;
    private int classNumber;
    private int audience;
}
