package org.letunov.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EducationDayDto
{
    private long id;
    private int weekNumber;
    private UserNamesDto userNamesDto;
    private List<Long> groupsId;
    private LocalDate date;
    private int classNumber;
    private int audience;
    private SubjectDto subject;
}
