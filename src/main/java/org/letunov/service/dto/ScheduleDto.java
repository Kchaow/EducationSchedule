package org.letunov.service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
public class ScheduleDto
{
    private List<ClassDto> classes;
    private List<String> dates;
}
