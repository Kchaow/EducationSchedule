package org.letunov.service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.letunov.domainModel.EducationDay;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
public class ScheduleDto
{
    private List<EducationDayDto> classes;
    private List<String> dates;
}
