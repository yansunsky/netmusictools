package com.ysk.netmusictools.controller;

import com.ysk.netmusictools.model.User;
import com.ysk.netmusictools.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @PostMapping("/doLogin")
    public String doLogin(
            @RequestParam("userName") String userName,
            @RequestParam(value = "qqNumber", required = false) String qqNumber,
            HttpServletRequest request,
            HttpServletResponse response) {

        User user = userService.login(userName, qqNumber, request);

        // 创建Cookie并设置30天有效期
        Cookie cookie = new Cookie("userName", user.getUserName());
        cookie.setMaxAge(30 * 24 * 60 * 60); // 30天
        cookie.setPath("/"); // 全局有效
        response.addCookie(cookie);

        return "index";
    }
}