package com.blog;

import com.blog.service.AdminService;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication(excludeName = {
    "org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchDataAutoConfiguration",
    "org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchRepositoriesAutoConfiguration"
})
@MapperScan("com.blog.mapper")
@EnableCaching
public class BlogApplication implements CommandLineRunner {

    private final AdminService adminService;

    public BlogApplication(AdminService adminService) {
        this.adminService = adminService;
    }

    public static void main(String[] args) {
        SpringApplication.run(BlogApplication.class, args);
    }

    @Override
    public void run(String... args) {
        adminService.createAdminIfNotExists();
    }
}
