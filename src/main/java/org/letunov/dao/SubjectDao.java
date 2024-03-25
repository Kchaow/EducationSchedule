package org.letunov.dao;

import org.letunov.domainModel.Subject;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface SubjectDao
{
    Subject findById(long id);
    List<Subject> findAll();
    Subject findByName(String name);
    Subject save(Subject subject);
    void deleteById(long id);
}
