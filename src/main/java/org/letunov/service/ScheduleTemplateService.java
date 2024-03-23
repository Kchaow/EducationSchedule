package org.letunov.service;

import org.letunov.domainModel.ScheduleTemplate;
import org.springframework.ui.Model;

import java.util.List;

public interface ScheduleTemplateService
{
    List<ScheduleTemplate> getScheduleTemplates();

    void addNewScheduleTemplate(ScheduleTemplate scheduleTemplate);
    void deleteScheduleTemplate(long id);
}
