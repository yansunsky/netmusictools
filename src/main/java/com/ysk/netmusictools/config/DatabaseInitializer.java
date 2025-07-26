package com.ysk.netmusictools.config;

import com.ysk.netmusictools.model.User;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Configuration
public class DatabaseInitializer {

    @Bean
    public CommandLineRunner initDatabase(JdbcTemplate jdbcTemplate) {
        return args -> {
            // 创建user表
            jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS `user` (" +
                    "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                    "userName VARCHAR(255) NOT NULL UNIQUE, " +
                    "userIp VARCHAR(255), " +
                    "userQQNumber VARCHAR(255)" +
                    ")");

            // 检查是否有测试数据，如果没有则添加一些测试数据
            List<User> users = jdbcTemplate.query("SELECT * FROM `user` LIMIT 1", new UserRowMapper());
            if (users.isEmpty()) {
                jdbcTemplate.update("INSERT INTO `user` (userName, userIp, userQQNumber) VALUES (?, ?, ?)",
                        "testUser", "127.0.0.1", "123456789");
                System.out.println("测试用户已创建");
            }
        };
    }

    // User行映射器
    private static class UserRowMapper implements RowMapper<User> {
        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            User user = new User();
            user.setId(rs.getLong("id"));
            user.setUserName(rs.getString("userName"));
            user.setUserIP(rs.getString("userIp"));
            user.setUserQQNumber(rs.getString("userQQNumber"));
            return user;
        }
    }
}