package com.blog.controller;

import com.blog.entity.Article;
import com.blog.entity.ArticleDocument;
import com.blog.entity.Category;
import com.blog.entity.Outline;
import com.blog.repository.ArticleSearchRepository;
import com.blog.service.ArticleService;
import com.blog.service.CategoryService;
import com.blog.service.OutlineService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PublicController {

    private static final Logger log = LoggerFactory.getLogger(PublicController.class);
    private static final String INIT_HOME_KEY = "init:home";
    private static final Duration CACHE_TTL = Duration.ofHours(1);

    private final CategoryService categoryService;
    private final OutlineService outlineService;
    private final ArticleService articleService;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    @Autowired(required = false)
    private ArticleSearchRepository articleSearchRepository;

    @GetMapping("/categories")
    public ResponseEntity<List<Category>> listCategories() {
        return ResponseEntity.ok(categoryService.listAll());
    }

    @GetMapping("/outlines")
    public ResponseEntity<List<Outline>> listOutlines(@RequestParam(required = false) Integer categoryId) {
        if (categoryId != null) {
            return ResponseEntity.ok(outlineService.listByCategoryId(categoryId));
        }
        return ResponseEntity.ok(outlineService.listAll());
    }

    @GetMapping("/articles")
    public ResponseEntity<List<Article>> listArticles(@RequestParam Integer outlineId) {
        return ResponseEntity.ok(articleService.listByOutlineId(outlineId));
    }

    @GetMapping("/init")
    public ResponseEntity<Map<String, Object>> initHome() {
        // 优先从 Redis 读取缓存（存储为 JSON 字符串）
        try {
            Object cached = redisTemplate.opsForValue().get(INIT_HOME_KEY);
            if (cached instanceof String) {
                Map<String, Object> cachedData = objectMapper.readValue(
                        (String) cached, new TypeReference<Map<String, Object>>() {});
                log.debug("从 Redis 缓存返回主页初始化数据");
                return ResponseEntity.ok(cachedData);
            }
        } catch (Exception e) {
            log.warn("Redis 读取失败，回退到数据库查询: {}", e.getMessage());
        }

        // 缓存未命中，从数据库查询
        List<Category> categories = categoryService.listAll();
        List<Outline> outlines = outlineService.listAll();
        Map<String, List<Map<String, Object>>> articlesByOutline = new HashMap<>();
        for (Outline outline : outlines) {
            List<Article> articles = articleService.listByOutlineId(outline.getId());
            // 只返回 id 和 title，不返回 content
            List<Map<String, Object>> articleTitles = new ArrayList<>();
            for (Article article : articles) {
                Map<String, Object> item = new HashMap<>();
                item.put("id", article.getId());
                item.put("title", article.getTitle());
                articleTitles.add(item);
            }
            articlesByOutline.put(outline.getId().toString(), articleTitles);
        }
        Map<String, Object> initData = new HashMap<>();
        initData.put("categories", categories);
        initData.put("outlines", outlines);
        initData.put("articlesByOutline", articlesByOutline);

        // 序列化为 JSON 字符串存入 Redis
        try {
            String json = objectMapper.writeValueAsString(initData);
            redisTemplate.opsForValue().set(INIT_HOME_KEY, json, CACHE_TTL);
        } catch (Exception e) {
            log.warn("Redis 缓存写入失败: {}", e.getMessage());
        }

        return ResponseEntity.ok(initData);
    }

    @GetMapping("/articles/{id}")
    public ResponseEntity<Article> getArticle(@PathVariable Integer id) {
        Article article = articleService.getById(id);
        if (article == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(article);
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchArticles(@RequestParam String keyword) {
        if (articleSearchRepository != null) {
            try {
                List<ArticleDocument> results = articleSearchRepository.findByTitleContaining(keyword);
                return ResponseEntity.ok(Map.of("results", results));
            } catch (Exception e) {
                // ES search failed, fall back to database
            }
        }
        List<Article> results = articleService.searchByKeyword(keyword);
        return ResponseEntity.ok(Map.of("results", results));
    }
}
