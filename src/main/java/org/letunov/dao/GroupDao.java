package org.letunov.dao;

import org.letunov.domainModel.Group;
import org.springframework.data.domain.Page;

import java.util.List;

public interface GroupDao
{
    Page<Group> findAllOrderByNameAsc();
    Group findById(long id);
    Group findByName(String name);
    void deleteById(long id);
    Group save(Group group);
}
