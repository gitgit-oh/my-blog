package com.blog.service;

import com.blog.entity.Category;
import com.blog.mapper.CategoryMapper;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryService {

    private static final String INIT_HOME_KEY = "init:home";

    private final CategoryMapper categoryMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * 清除主页初始化缓存
     */
    private void evictInitCache() {
        try {
            redisTemplate.delete(INIT_HOME_KEY);
        } catch (Exception e) {
            log.warn("清除主页初始化缓存失败: {}", e.getMessage());
        }
    }

    @Cacheable(value = "categories")
    public List<Category> listAll() {
        return categoryMapper.findAll();
    }

    public Category getById(Integer id) {
        return categoryMapper.findById(id);
    }

    @CacheEvict(value = "categories", allEntries = true)
    public void create(Category category) {
        categoryMapper.insert(category);
        evictInitCache();
    }

    @CacheEvict(value = "categories", allEntries = true)
    public void update(Category category) {
        categoryMapper.update(category);
        evictInitCache();
    }

    @CacheEvict(value = "categories", allEntries = true)
    public void delete(Integer id) {
        categoryMapper.deleteById(id);
        evictInitCache();
    }
}
