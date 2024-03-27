package org.letunov.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.letunov.dao.ScheduleTemplateDao;
import org.letunov.domainModel.ScheduleTemplate;
import org.letunov.service.ScheduleService;
import org.letunov.service.ScheduleTemplateService;
import org.springframework.http.ResponseEntity;
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
    public ScheduleTemplate getScheduleTemplate(long id)
    {
        return scheduleTemplateDao.findById(id);
    }

    @Override
    public List<ScheduleTemplate> getScheduleTemplates()
    {
        return scheduleTemplateDao.findAll();
    }

    @Override
    public void addNewScheduleTemplate(ScheduleTemplate scheduleTemplate)
    {
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
        List<ScheduleTemplate> activeScheduleTemplate = scheduleTemplateDao.findByIsActive(true);
        if (activeScheduleTemplate != null)
        {
            for (ScheduleTemplate active : activeScheduleTemplate)
            {
                active.setActive(false);
                scheduleTemplateDao.save(active);
            }
        }
        ScheduleTemplate inactiveScheduleTemplate = scheduleTemplateDao.findByName(templateName);
        inactiveScheduleTemplate.setActive(true);
        scheduleTemplateDao.save(inactiveScheduleTemplate);
    }

    @Override
    public ResponseEntity<ScheduleTemplate> getActiveScheduleTemplate()
    {
        ScheduleTemplate scheduleTemplate = scheduleTemplateDao.findByIsActive(true).getFirst();
        return ResponseEntity.ok(scheduleTemplate);
    }
}
