package org.letunov.dao;

import org.letunov.domainModel.ScheduleTemplate;

import java.util.List;

public interface ScheduleTemplateDao
{
    List<ScheduleTemplate> findAll();
    ScheduleTemplate findById(long id);
    ScheduleTemplate findByName(String name);
    List<ScheduleTemplate> findByIsActive(boolean isActive);
    ScheduleTemplate save(ScheduleTemplate templateSchedule);
    void deleteById(long id);
}
