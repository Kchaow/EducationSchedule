package org.letunov.dao;

import org.letunov.domainModel.Group;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface GroupDao
{
    Page<Group> findAllOrderByNameAsc(int size, int page);
    Group findByUserId(long id);
    Group findById(long id);
    Group findByName(String name);
    void deleteById(long id);
    Group save(Group group);
}
