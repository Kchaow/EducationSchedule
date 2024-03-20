package org.letunov.service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
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
    private int dayOfWeek;
    private int classNumber;
    private int audience;
    private SubjectDto subject;
}
