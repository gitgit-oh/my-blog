package com.blog.config;

import com.blog.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final AdminService adminService;

    @Override
    public void run(String... args) {
        adminService.createAdminIfNotExists();
    }
}
