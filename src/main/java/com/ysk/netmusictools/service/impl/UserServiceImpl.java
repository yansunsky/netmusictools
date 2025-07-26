package com.ysk.netmusictools.service.impl;

import com.ysk.netmusictools.model.User;
import com.ysk.netmusictools.repository.UserRepository;
import com.ysk.netmusictools.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.servlet.http.HttpServletRequest;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public User login(String userName, String qqNumber, HttpServletRequest request) {
        // 查询用户是否存在
        return userRepository.findByUserName(userName)
                .map(existingUser -> {
                    // 更新用户IP
                    existingUser.setUserIP(getUserIP(request));
                    return userRepository.save(existingUser);
                })
                .orElseGet(() -> {
                    // 创建新用户
                    User newUser = new User();
                    newUser.setUserName(userName);
                    newUser.setUserIP(getUserIP(request));
                    newUser.setUserQQNumber(qqNumber);
                    return userRepository.save(newUser);
                });
    }

    // 获取用户IP地址
    private String getUserIP(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 处理多个IP的情况，只取第一个
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0];
        }
        return ip;
    }
}