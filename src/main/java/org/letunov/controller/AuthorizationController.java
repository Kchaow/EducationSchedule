package org.letunov.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class AuthorizationController
{

    @GetMapping("login")
    public String getLoginPage()
    {
        return "login";
    }
}
