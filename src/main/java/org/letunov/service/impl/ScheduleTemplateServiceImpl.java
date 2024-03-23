package org.letunov.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.letunov.dao.ScheduleTemplateDao;
import org.letunov.domainModel.ScheduleTemplate;
import org.letunov.service.ScheduleService;
import org.letunov.service.ScheduleTemplateService;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.List;

@Slf4j
@Service
public class ScheduleTemplateServiceImpl implements ScheduleTemplateService
{
    private final ScheduleTemplateDao scheduleTemplateDao;

    public ScheduleTemplateServiceImpl(ScheduleTemplateDao scheduleTemplateDao)
    {
        this.scheduleTemplateDao = scheduleTemplateDao;
    }

    @Override
    public List<ScheduleTemplate> getScheduleTemplates()
    {
        return scheduleTemplateDao.findAll();
    }

    @Override
    public void addNewScheduleTemplate(ScheduleTemplate scheduleTemplate)
    {
        log.info("%s object is adding".formatted(scheduleTemplate.toString()));
        scheduleTemplateDao.save(scheduleTemplate);
    }

    @Override
    public void deleteScheduleTemplate(long id)
    {
        scheduleTemplateDao.deleteById(id);
    }
}
