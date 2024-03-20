package org.letunov.controller;

import org.letunov.dao.UserDao;
import org.letunov.domainModel.User;
import org.letunov.service.AttendanceService;
import org.letunov.service.dto.AttendanceDto;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.thymeleaf.extras.springsecurity6.util.SpringSecurityContextUtils;

@Controller
@RequestMapping("/attendance")
public class AttendanceController
{
    final private AttendanceService attendanceService;
    final private UserDao userDao;

    public AttendanceController(AttendanceService attendanceService, UserDao userDao)
    {
        this.attendanceService = attendanceService;
        this.userDao = userDao;
    }

    @GetMapping("/{userId}/{classId}")
    public ResponseEntity<AttendanceDto> getAttendance(@PathVariable("userId") long userId,@PathVariable("classId") long classId)
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (userDao.findByLogin(((UserDetails) authentication.getPrincipal()).getUsername()).getId() == userId
                || authentication.getAuthorities().stream().anyMatch(auth -> auth.getAuthority().equals("admin") || auth.getAuthority().equals("teacher")))
        {
            return attendanceService.getAttendance(userId, classId);
        }
        else
        {
            throw new RuntimeException("No permission");
        }
    }
}
