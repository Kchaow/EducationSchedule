package org.letunov.service.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Data
@Builder
public class EducationDayDto
{
    private int weekNumber;
    private UserNamesDto userNamesDto;
    private List<Long> groupsId;
    private LocalDate date;
    private int classNumber;
    private int audience;
}
