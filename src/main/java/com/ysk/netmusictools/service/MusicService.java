package com.ysk.netmusictools.service;


import com.ysk.netmusictools.model.Music;
import org.springframework.web.multipart.MultipartFile;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.IOException;

public interface MusicService {
    /**
     * 上传音乐并返回播放链接
     */
    String uploadMusic(MultipartFile file,String userName, HttpServletRequest request) throws IOException;

    /**
     * 获取用户最新上传的音乐（用于查询文件名）
     */
    Music getLatestMusic(String userName);

    Page<Music> searchMusic(String keyword, Pageable pageable);

}