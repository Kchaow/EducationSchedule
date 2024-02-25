package org.letunov.dao;

import org.letunov.domainModel.Subject;
import org.springframework.data.domain.Page;

public interface SubjectDao
{
    Subject findById(long id);
    Page<Subject> findAll();
    Subject save(Subject subject);
    void deleteById(long id);
}
