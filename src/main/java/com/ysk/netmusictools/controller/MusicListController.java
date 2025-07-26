package com.ysk.netmusictools.controller;

import com.ysk.netmusictools.model.Music;
import com.ysk.netmusictools.service.MusicService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/view")
public class MusicViewController {

    private final MusicService musicService;

    public MusicViewController(MusicService musicService) {
        this.musicService = musicService;
    }

    // 构造器注入

    @GetMapping("/musicList")
    public String musicList(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            Model model) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        Page<Music> musicPage = musicService.searchMusic(keyword, pageable);


        return "musicList"; // 对应HTML模板
    }
}