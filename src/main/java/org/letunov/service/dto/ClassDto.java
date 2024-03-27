package org.letunov.service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.letunov.domainModel.Group;
import org.letunov.domainModel.Subject;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ClassDto
{
    private long id;
    private int weekNumber;
    private UserNamesDto userNamesDto;
    private List<Group> groups;
    private int dayOfWeek;
    private int classNumber;
    private int audience;
    private Subject subject;
    private long scheduleTemplateId;
}
