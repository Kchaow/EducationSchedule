package org.letunov.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.letunov.dao.ScheduleTemplateDao;
import org.letunov.domainModel.ScheduleTemplate;
import org.letunov.service.ScheduleService;
import org.letunov.service.ScheduleTemplateService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;

import java.util.List;
import java.util.NoSuchElementException;

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

    @Transactional
    @Override
    public void makeTemplateActive(String templateName)
    {
        ScheduleTemplate activeScheduleTemplate = scheduleTemplateDao.findByIsActive(true).getFirst();
        activeScheduleTemplate.setActive(false);
        scheduleTemplateDao.save(activeScheduleTemplate);
        ScheduleTemplate inactiveScheduleTemplate = scheduleTemplateDao.findByName(templateName);
        inactiveScheduleTemplate.setActive(true);
        scheduleTemplateDao.save(inactiveScheduleTemplate);
    }
}
