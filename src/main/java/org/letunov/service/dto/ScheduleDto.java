package org.letunov.service.dto;

import lombok.Data;
import org.letunov.domainModel.EducationDay;

import java.util.ArrayList;
import java.util.List;

@Data
public class ScheduleDto
{
    private List<EducationDayDto> classes = new ArrayList<>();
}
