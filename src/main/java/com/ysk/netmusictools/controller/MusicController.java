package com.ysk.netmusictools.controller;

import com.ysk.netmusictools.model.Music;
import com.ysk.netmusictools.service.MusicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;

@Controller
@RequestMapping("/")
public class MusicController {

    @Autowired
    private MusicService musicService;

    /**
     * 处理音乐上传请求
     */
    @PostMapping("/api/music/upload")
    public String uploadMusic(@RequestParam("file") MultipartFile file, Model model, HttpServletRequest request) {
        try {
            // 从Cookie中获取用户名
            String userName = getUserNameFromCookie(request);
            if (userName == null || userName.trim().isEmpty()) {
                throw new IllegalArgumentException("用户未登录，请先登录后再上传音乐");
            }

            String playUrl = musicService.uploadMusic(file, userName, request);
            // 查询刚上传的音乐信息
            Music music = musicService.getLatestMusic(userName);
            // 传递成功消息和数据到前端
            model.addAttribute("success", "音乐上传成功！");
            model.addAttribute("fileName", music.getMusicName());
            model.addAttribute("musicTime", music.getDurationSec());
            model.addAttribute("playUrl", playUrl);
            model.addAttribute("downloadUrl", playUrl);
            return "index";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "index";
        }
    }

    /**
     * 从Cookie中获取用户名（工具方法）
     */
    private String getUserNameFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("userName".equals(cookie.getName())) { // 匹配Cookie名称
                    return cookie.getValue();
                }
            }
        }
        return null; // 未找到Cookie
    }
}