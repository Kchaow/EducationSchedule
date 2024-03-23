package org.letunov.domainModel;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
public class ScheduleTemplate extends AbstractEntity
{
    private String name;
    private LocalDate startDate;
    private int weekCount;
    private boolean isActive;
}
