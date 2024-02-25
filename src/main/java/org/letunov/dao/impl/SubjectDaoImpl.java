package org.letunov.dao.impl;

import org.letunov.dao.SubjectDao;
import org.letunov.domainModel.Subject;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

@Repository
public class SubjectDaoImpl implements SubjectDao
{
    @Override
    public Subject findById(long id) {
        return null;
    }

    @Override
    public Page<Subject> findAll() {
        return null;
    }

    @Override
    public Subject save(Subject subject) {
        return null;
    }

    @Override
    public void deleteById(long id) {

    }
}
