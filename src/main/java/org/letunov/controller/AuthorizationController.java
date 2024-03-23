package org.letunov.controller;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.letunov.dao.UserDao;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class AuthorizationController
{
    final private UserDao userDao;

    public AuthorizationController(UserDao userDao)
    {
        this.userDao = userDao;
    }

    @GetMapping("login")
    public String getLoginPage()
    {
        return "login";
    }

    @GetMapping("/currentUserId")
    public ResponseEntity<UserId> getCurrentUserId()
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null)
        {
            long id = userDao.findByLogin(((UserDetails) authentication.getPrincipal()).getUsername()).getId();
            UserId userId = new UserId(id);
            return new ResponseEntity<UserId>(userId, HttpStatus.OK);
        }
        else
        {
            return new ResponseEntity<UserId>(new UserId(-1), HttpStatus.FORBIDDEN);
        }
    }

    @Getter
    @Setter
    @AllArgsConstructor
    static class UserId
    {
        private long id;
    }
}
