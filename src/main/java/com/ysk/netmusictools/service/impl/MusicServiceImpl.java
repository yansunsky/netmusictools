package com.ysk.netmusictools.service.impl;

import com.ysk.netmusictools.model.Music;
import com.ysk.netmusictools.repository.MusicRepository;
import com.ysk.netmusictools.service.MusicService;
import jakarta.persistence.criteria.Predicate;
import jakarta.servlet.http.Cookie;
import org.jaudiotagger.audio.*;
import org.jaudiotagger.audio.exceptions.*;
import org.jaudiotagger.tag.TagException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;
import jakarta.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class MusicServiceImpl implements MusicService {

    @Value("${file.upload-dir:music}")
    private String uploadDir;

    private final MusicRepository musicRepository;

    public MusicServiceImpl(MusicRepository musicRepository) {
        this.musicRepository = musicRepository;
    }

    @Override
    public String uploadMusic(MultipartFile file,String userName, HttpServletRequest request) throws IOException {
        // 校验文件类型（兼容"audio/mp3"和"audio/mpeg"）
        String contentType = file.getContentType();
        if (!("audio/mp3".equals(contentType) || "audio/mpeg".equals(contentType))) {
            throw new IllegalArgumentException("仅支持MP3格式文件");
        }

        // 校验文件大小（≤10MB）
        if (file.getSize() > 10 * 1024 * 1024) {
            throw new IllegalArgumentException("文件大小不能超过10MB");
        }

        // 创建用户专属存储目录（static/music/用户名）
        String userDirPath = uploadDir + "/" + userName; // 例如：./uploads/music/当前用户名
        File userDir = new File(userDirPath);
        if (!userDir.exists()) {
            boolean created = userDir.mkdirs(); // 递归创建目录（包括父目录）
            if (!created) {
                throw new IOException("用户目录创建失败：" + userDirPath);
            }
        }

        // 生成唯一文件名（UUID.mp3）
        String originalFilename = file.getOriginalFilename();
        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String uuid = UUID.randomUUID().toString();
        String newFilename = uuid + fileExtension;



        // 保存文件到本地
        String targetPath = userDirPath + "/" + newFilename;
        Files.copy(file.getInputStream(), Paths.get(targetPath), StandardCopyOption.REPLACE_EXISTING);

        // === 在服务内直接解析时长 ===
        int durationSec = parseAudioDuration(targetPath);

        // 保存文件信息到数据库
        Music music = new Music();
        music.setMusicName(originalFilename);
        music.setUserName(userName);
        music.setFilePath("/" + uploadDir + "/" + userName + "/" + newFilename);
        music.setDurationSec(durationSec); // 设置时长
        musicRepository.save(music);

        // 动态生成播放链接（基于当前请求的域名/IP）
        return generateAccessLink(request, music.getFilePath());
    }

    @Override
    public Music getLatestMusic(String userName) {
        // 查询用户最新上传的音乐（按id降序取第一条）
        return musicRepository.findTopByUserNameOrderByIdDesc(userName);
    }

    /**
     * 分页搜索音乐（支持关键词模糊查询）
     */
    @Override
    public Page<Music> searchMusic(String keyword, Pageable pageable) {
        // 构建动态查询条件（模糊匹配文件名或用户名）
        Specification<Music> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (StringUtils.hasText(keyword)) {
                String lowerKeyword = keyword.toLowerCase();// 不区分大小写模糊匹配文件名或用户名
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("musicName")), "%" + lowerKeyword + "%"),
                        cb.like(cb.lower(root.get("userName")), "%" + lowerKeyword + "%")
                ));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        // 执行分页查询
        return musicRepository.findAll(spec, pageable);
    }

    private String generateAccessLink(HttpServletRequest request, String filePath) {
        // 使用ServletUriComponentsBuilder构建完整URL
        return ServletUriComponentsBuilder.fromRequestUri(request)
                .replacePath(null)
                .path(filePath)
                .toUriString();
    }

    private int parseAudioDuration(String filePath) {
        try {
            File audioFile = new File(filePath);
            AudioFile audio = AudioFileIO.read(audioFile);
            return audio.getAudioHeader().getTrackLength(); // 返回秒数
        } catch (CannotReadException | IOException | TagException |
                 ReadOnlyFileException | InvalidAudioFrameException e) {
            // 日志记录错误或使用默认值
            System.err.println("音频时长解析失败: " + e.getMessage());
            return 0; // 返回0作为默认值
        }
    }
}