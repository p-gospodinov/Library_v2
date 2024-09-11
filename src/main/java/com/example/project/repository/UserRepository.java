package com.example.project.repository;

import com.example.project.enums.Role;
import com.example.project.enums.UserStatus;
import com.example.project.model.User;
import com.example.project.security.PasswordEncoderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public class UserRepository {
    private final JdbcTemplate jdbcTemplate;

    private final PasswordEncoderService passwordEncoder;

    public UserRepository(JdbcTemplate jdbcTemplate, PasswordEncoderService passwordEncoder) {
        this.jdbcTemplate = jdbcTemplate;
        this.passwordEncoder = passwordEncoder;
    }

    private static final class UserRowMapper implements RowMapper<User> {
        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            User user = new User();
            user.setUserID(rs.getInt("userID"));
            user.setUsername(rs.getString("username"));
            user.setPassword(rs.getString("password"));
            user.setRole(Role.valueOf(rs.getString("role")));
            user.setUserStatus(UserStatus.valueOf(rs.getString("status")));
            user.setSuspensionEndTime(rs.getObject("suspensionEndTime", LocalDateTime.class));
            return user;
        }
    }

    public List<User> findAll() {
        String sql = "SELECT * FROM user";
        return jdbcTemplate.query(sql, new UserRepository.UserRowMapper());
    }

    public User findById(int id) {
        String sql = "SELECT " + "userID," +
                "    username," +
                "    password," +
                "    role, " +
                "    status, " +
                "    suspensionEndTime " +
                "FROM user " +
                "WHERE userID = ?";
        return jdbcTemplate.queryForObject(sql, new UserRepository.UserRowMapper(), id);
    }

    public User findByUsername(String username) {
        String sql = "SELECT " + "userID," +
                "    username," +
                "    password," +
                "    role, " +
                "    status, " +
                "    suspensionEndTime " +
                "FROM user " +
                "WHERE username = ?";
        return jdbcTemplate.queryForObject(sql, new UserRepository.UserRowMapper(), username);
    }

    public List<User> findSuspendedUsers() {
        String sql = "SELECT userID, username, password, role, status, suspensionEndTime FROM user WHERE status = ? AND suspensionEndTime IS NOT NULL";
        return jdbcTemplate.query(sql, new UserRepository.UserRowMapper(), UserStatus.SUSPENDED.name());
    }

    public int insert(User user) {
        String encodedPassword = passwordEncoder.encodePassword(user.getPassword());
        String sql = "INSERT INTO user (username, password, role, status, suspensionEndTime) VALUES (?, ?, ?, ?, ?)";
        return jdbcTemplate.update(sql, user.getUsername(), encodedPassword, user.getRole().name(), user.getUserStatus().name(), user.getSuspensionEndTime());
    }

    public int update(User user) {
        String sql = "UPDATE user SET username = ?, password = ?, role = ?, status = ? , suspensionEndTime = ? WHERE userID = ?";
        return jdbcTemplate.update(sql, user.getUsername(), user.getPassword(), user.getRole().name(), user.getUserStatus().name(), user.getSuspensionEndTime(), user.getUserID());
    }

    public int deleteById(int id) {
        String sql = "DELETE FROM user WHERE userID = ?";
        return jdbcTemplate.update(sql, id);
    }
}
