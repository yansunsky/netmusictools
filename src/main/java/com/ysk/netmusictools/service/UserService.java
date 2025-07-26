package com.ysk.netmusictools.service;

import com.ysk.netmusictools.model.User;
import jakarta.servlet.http.HttpServletRequest;

public interface UserService {
    User login(String userName, String qqNumber, HttpServletRequest request);
}