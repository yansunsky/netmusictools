package com.ysk.netmusictools.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "`user`") // 使用反引号避免MySQL关键字冲突
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String userName;

    private String userIP;

    private String userQQNumber;
}