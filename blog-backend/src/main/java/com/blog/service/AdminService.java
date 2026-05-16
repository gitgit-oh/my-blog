package com.blog.service;

import com.blog.entity.Admin;
import com.blog.mapper.AdminMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final AdminMapper adminMapper;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public Admin findByUsername(String username) {
        return adminMapper.findByUsername(username);
    }

    public boolean checkPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    public void createAdminIfNotExists() {
        Admin existing = adminMapper.findByUsername("admin");
        if (existing == null) {
            Admin admin = new Admin();
            admin.setUsername("admin");
            admin.setPasswordHash(passwordEncoder.encode("admin123"));
            adminMapper.insert(admin);
        }
    }

    public boolean changePassword(String username, String oldPassword, String newPassword) {
        Admin admin = adminMapper.findByUsername(username);
        if (admin == null || !checkPassword(oldPassword, admin.getPasswordHash())) {
            return false;
        }
        String newHash = passwordEncoder.encode(newPassword);
        adminMapper.updatePassword(username, newHash);
        return true;
    }
}
