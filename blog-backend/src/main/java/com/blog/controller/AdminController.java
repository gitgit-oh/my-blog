package com.blog.controller;

import com.blog.entity.*;
import com.blog.service.*;
import com.blog.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AdminController {

    private final AdminService adminService;
    private final CategoryService categoryService;
    private final OutlineService outlineService;
    private final ArticleService articleService;
    private final JwtUtil jwtUtil;

    @Value("${blog.upload.path}")
    private String uploadPath;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        String username = credentials.get("username");
        String password = credentials.get("password");
        Admin admin = adminService.findByUsername(username);
        if (admin == null || !adminService.checkPassword(password, admin.getPasswordHash())) {
            return ResponseEntity.status(401).body(Map.of("message", "Invalid credentials"));
        }
        String token = jwtUtil.generateToken(username);
        return ResponseEntity.ok(Map.of("token", token));
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        int categories = categoryService.listAll().size();
        int outlines = outlineService.listAll().size();
        int articles = articleService.countAll();
        return ResponseEntity.ok(Map.of(
            "categories", categories,
            "outlines", outlines,
            "articles", articles
        ));
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestHeader("Authorization") String authHeader,
                                            @RequestBody Map<String, String> body) {
        String token = authHeader.replace("Bearer ", "");
        String username = jwtUtil.extractUsername(token);
        String oldPassword = body.get("oldPassword");
        String newPassword = body.get("newPassword");
        boolean success = adminService.changePassword(username, oldPassword, newPassword);
        if (success) {
            return ResponseEntity.ok(Map.of("message", "Password changed successfully"));
        } else {
            return ResponseEntity.status(400).body(Map.of("message", "Invalid old password"));
        }
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            File dir = new File(uploadPath);
            if (!dir.exists()) dir.mkdirs();
            String ext = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
            String filename = UUID.randomUUID() + ext;
            Path path = Paths.get(uploadPath, filename);
            Files.write(path, file.getBytes());
            return ResponseEntity.ok(Map.of("url", "/upload/" + filename));
        } catch (IOException e) {
            return ResponseEntity.badRequest().body(Map.of("message", "Upload failed"));
        }
    }

    // Categories
    @PostMapping("/categories")
    public ResponseEntity<?> createCategory(@RequestBody Category category) {
        categoryService.create(category);
        return ResponseEntity.ok(category);
    }

    @PutMapping("/categories/{id}")
    public ResponseEntity<?> updateCategory(@PathVariable Integer id, @RequestBody Category category) {
        category.setId(id);
        categoryService.update(category);
        return ResponseEntity.ok(category);
    }

    @DeleteMapping("/categories/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable Integer id) {
        categoryService.delete(id);
        return ResponseEntity.ok(Map.of("message", "Deleted"));
    }

    // Outlines
    @PostMapping("/outlines")
    public ResponseEntity<?> createOutline(@RequestBody Outline outline) {
        outlineService.create(outline);
        return ResponseEntity.ok(outline);
    }

    @PutMapping("/outlines/{id}")
    public ResponseEntity<?> updateOutline(@PathVariable Integer id, @RequestBody Outline outline) {
        outline.setId(id);
        outlineService.update(outline);
        return ResponseEntity.ok(outline);
    }

    @DeleteMapping("/outlines/{id}")
    public ResponseEntity<?> deleteOutline(@PathVariable Integer id) {
        outlineService.delete(id);
        return ResponseEntity.ok(Map.of("message", "Deleted"));
    }

    // Articles
    @GetMapping("/articles/page")
    public ResponseEntity<?> listArticlesPage(@RequestParam(defaultValue = "1") int page,
                                              @RequestParam(defaultValue = "10") int size,
                                              @RequestParam(required = false) Integer outlineId) {
        return ResponseEntity.ok(articleService.listByPage(outlineId, page, size));
    }

    @PostMapping("/articles")
    public ResponseEntity<?> createArticle(@RequestBody Article article) {
        articleService.create(article);
        return ResponseEntity.ok(article);
    }

    @PutMapping("/articles/{id}")
    public ResponseEntity<?> updateArticle(@PathVariable Integer id, @RequestBody Article article) {
        article.setId(id);
        articleService.update(article);
        return ResponseEntity.ok(article);
    }

    @DeleteMapping("/articles/{id}")
    public ResponseEntity<?> deleteArticle(@PathVariable Integer id) {
        articleService.delete(id);
        return ResponseEntity.ok(Map.of("message", "Deleted"));
    }
}
