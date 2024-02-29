package org.letunov.dao;

import org.letunov.domainModel.Subject;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Repository;

public interface SubjectDao
{
    Subject findById(long id);
    Page<Subject> findAll(int limit, int offset);
    Subject findByName(String name);
    Subject save(Subject subject);
    void deleteById(long id);
}
