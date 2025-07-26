package com.ysk.netmusictools.repository;


import com.ysk.netmusictools.model.Music;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MusicRepository extends JpaRepository<Music, Long> {
    /**
     * 根据用户名查询最新上传的音乐（按ID降序）
     * @param userName 用户名
     * @return 最新上传的音乐记录
     */
    Music findTopByUserNameOrderByIdDesc(String userName);

    Page<Music> findAll(Specification<Music> spec, Pageable pageable);
}