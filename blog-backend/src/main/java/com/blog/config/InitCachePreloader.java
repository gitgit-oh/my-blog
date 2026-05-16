package com.blog.config;

import com.blog.entity.Article;
import com.blog.entity.Category;
import com.blog.entity.Outline;
import com.blog.mapper.ArticleMapper;
import com.blog.service.CategoryService;
import com.blog.service.OutlineService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 应用启动时预加载主页初始化数据到 Redis，
 * 提升首页首次访问速度。
 * 在 RedisCacheClearer（Order=1）之后执行。
 */
@Component
@Order(2)
public class InitCachePreloader implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(InitCachePreloader.class);
    private static final String INIT_HOME_KEY = "init:home";
    private static final Duration CACHE_TTL = Duration.ofHours(1);

    private final RedisTemplate<String, Object> redisTemplate;
    private final CategoryService categoryService;
    private final OutlineService outlineService;
    private final ArticleMapper articleMapper;
    private final ObjectMapper objectMapper;

    public InitCachePreloader(RedisTemplate<String, Object> redisTemplate,
                              CategoryService categoryService,
                              OutlineService outlineService,
                              ArticleMapper articleMapper,
                              ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.categoryService = categoryService;
        this.outlineService = outlineService;
        this.articleMapper = articleMapper;
        this.objectMapper = objectMapper;
    }

    @Override
    public void run(String... args) {
        try {
            // 检查缓存是否已存在
            if (Boolean.TRUE.equals(redisTemplate.hasKey(INIT_HOME_KEY))) {
                log.info("✓ 主页初始化缓存已存在，跳过预加载");
                return;
            }
            preloadInitCache();
        } catch (Exception e) {
            log.warn("✗ 主页初始化缓存预加载失败（可能 Redis 不可用）: {}", e.getMessage());
        }
    }

    /**
     * 查询数据库并构建主页初始化数据，存入 Redis。
     */
    public void preloadInitCache() {
        List<Category> categories = categoryService.listAll();
        List<Outline> outlines = outlineService.listAll();
        Map<String, List<Map<String, Object>>> articlesByOutline = new HashMap<>();

        for (Outline outline : outlines) {
            List<Article> articles = articleMapper.findByOutlineId(outline.getId());
            List<Map<String, Object>> articleTitles = new ArrayList<>();
            for (Article article : articles) {
                articleTitles.add(Map.of(
                        "id", article.getId(),
                        "title", article.getTitle()
                ));
            }
            articlesByOutline.put(outline.getId().toString(), articleTitles);
        }

        Map<String, Object> initData = new HashMap<>();
        initData.put("categories", categories);
        initData.put("outlines", outlines);
        initData.put("articlesByOutline", articlesByOutline);

        try {
            String json = objectMapper.writeValueAsString(initData);
            redisTemplate.opsForValue().set(INIT_HOME_KEY, json, CACHE_TTL);
            log.info("✓ 主页初始化数据已预加载到 Redis（categories: {}, outlines: {}, articlesByOutline: {}）",
                    categories.size(), outlines.size(), articlesByOutline.size());
        } catch (Exception e) {
            log.error("✗ 主页初始化数据序列化失败: {}", e.getMessage());
        }
    }
}
