package org.letunov.service.impl;

import org.letunov.dao.SubjectDao;
import org.letunov.domainModel.Subject;
import org.letunov.service.SubjectService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubjectServiceImpl implements SubjectService
{
    final private SubjectDao subjectDao;

    public SubjectServiceImpl(SubjectDao subjectDao)
    {
        this.subjectDao = subjectDao;
    }


    @Override
    public List<Subject> getSubjectsList()
    {
        return  subjectDao.findAll();
    }
}
