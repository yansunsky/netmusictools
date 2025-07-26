package com.ysk.netmusictools.controller;

import com.ysk.netmusictools.model.Music;
import com.ysk.netmusictools.service.MusicService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/api/music")
public class MusicListController {

    private final MusicService musicService;

    public MusicListController(MusicService musicService) {
        this.musicService = musicService;
    }
    @GetMapping("/musicList")
    public String musicList(
            @RequestParam(value = "keyword", required = false, defaultValue = "") String keyword,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            Model model,
            HttpServletRequest request) {


        Page<Music> musicPage = musicService.searchMusic(
                keyword,
                PageRequest.of(
                        page,
                        size,
                        Sort.by(Sort.Direction.ASC, "id")));

        int currentPage = page + 1;
        int totalPages =  musicPage.getTotalPages();
        String currentUrl = getCurrentAccessUrl(request);
        // 将数据传递给前端模板
        model.addAttribute("musicList", musicPage.getContent());
        model.addAttribute("currentPage", page + 1);  // 转换为前端1-based页码
        model.addAttribute("totalPages", musicPage.getTotalPages());
        model.addAttribute("totalElements", musicPage.getTotalElements());
        model.addAttribute("keyword", keyword);
        model.addAttribute("baseUrl", currentUrl);
        return "musicList";  // 渲染templates/musicList.html
    }
    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> getMusicList(
            @RequestParam(value = "keyword", required = false) String keyword
    ){
        return null;
    }

    /**
     * 动态获取用户访问的完整URL（协议+域名/IP+端口）
     * @param request 当前HTTP请求对象
     * @return 完整访问URL
     */
    private String getCurrentAccessUrl(HttpServletRequest request) {
        // 获取协议（http/https）
        String scheme = request.getScheme();
        // 获取域名或IP
        String serverName = request.getServerName();
        // 获取端口号（注意：默认端口可能返回0，需处理）
        int port = request.getServerPort();

        // 处理端口显示逻辑（可选）：隐藏默认端口（https默认443，http默认80）
        if ((scheme.equals("https") && port == 443) || (scheme.equals("http") && port == 80)) {
            return String.format("%s://%s", scheme, serverName);
        } else {
            return String.format("%s://%s:%d", scheme, serverName, port);
        }
    }
}