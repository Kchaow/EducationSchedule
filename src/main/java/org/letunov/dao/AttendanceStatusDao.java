package org.letunov.dao;

import org.letunov.domainModel.AttendanceStatus;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface AttendanceStatusDao
{
    List<AttendanceStatus> findAll();
    AttendanceStatus findById(long id);
    AttendanceStatus findByName(String name);
}
