package com.blog.service;

import com.blog.entity.Outline;
import com.blog.mapper.OutlineMapper;
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
public class OutlineService {

    private static final String INIT_HOME_KEY = "init:home";

    private final OutlineMapper outlineMapper;
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

    @Cacheable(value = "outlines", key = "'all'")
    public List<Outline> listAll() {
        return outlineMapper.findAll();
    }

    @Cacheable(value = "outlines", key = "#categoryId")
    public List<Outline> listByCategoryId(Integer categoryId) {
        return outlineMapper.findByCategoryId(categoryId);
    }

    public Outline getById(Integer id) {
        return outlineMapper.findById(id);
    }

    @CacheEvict(value = "outlines", allEntries = true)
    public void create(Outline outline) {
        outlineMapper.insert(outline);
        evictInitCache();
    }

    @CacheEvict(value = "outlines", allEntries = true)
    public void update(Outline outline) {
        outlineMapper.update(outline);
        evictInitCache();
    }

    @CacheEvict(value = "outlines", allEntries = true)
    public void delete(Integer id) {
        outlineMapper.deleteById(id);
        evictInitCache();
    }
}
