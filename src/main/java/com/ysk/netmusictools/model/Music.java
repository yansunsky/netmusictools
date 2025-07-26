package com.ysk.netmusictools.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "music")
@Data
public class Music {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;                  // 自增主键

    @Column(nullable = false)
    private String musicName;         // 原始文件名（如"song.mp3"）

    @Column(nullable = false)
    private String userName;          // 上传者用户名

    @Column(nullable = false)
    private String filePath;          // 存储路径（如"/music/username/uuid.mp3"）

    @Column(nullable = false)
    private int durationSec;          // 音乐时长
}