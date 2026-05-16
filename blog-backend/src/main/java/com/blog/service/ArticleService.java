package com.blog.service;

import com.blog.entity.Article;
import com.blog.entity.ArticleDocument;
import com.blog.mapper.ArticleMapper;
import com.blog.repository.ArticleSearchRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class ArticleService {

    private static final String INIT_HOME_KEY = "init:home";

    private final ArticleMapper articleMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    @Autowired(required = false)
    private ArticleSearchRepository articleSearchRepository;

    public ArticleService(ArticleMapper articleMapper, RedisTemplate<String, Object> redisTemplate) {
        this.articleMapper = articleMapper;
        this.redisTemplate = redisTemplate;
    }

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

    public List<Article> listByOutlineId(Integer outlineId) {
        return articleMapper.findByOutlineId(outlineId);
    }

    @Cacheable(value = "articles", key = "#id")
    public Article getById(Integer id) {
        return articleMapper.findById(id);
    }

    @CacheEvict(value = "articles", allEntries = true)
    public void create(Article article) {
        articleMapper.insert(article);
        evictInitCache();
        if (articleSearchRepository != null) {
            try {
                ArticleDocument doc = new ArticleDocument();
                doc.setId(article.getId());
                doc.setOutlineId(article.getOutlineId());
                doc.setTitle(article.getTitle());
                doc.setContent(article.getContent());
                articleSearchRepository.save(doc);
            } catch (Exception e) {
                log.warn("Elasticsearch index failed: {}", e.getMessage());
            }
        }
    }

    @CacheEvict(value = "articles", allEntries = true)
    public void update(Article article) {
        articleMapper.update(article);
        evictInitCache();
        if (articleSearchRepository != null) {
            try {
                ArticleDocument doc = new ArticleDocument();
                doc.setId(article.getId());
                doc.setOutlineId(article.getOutlineId());
                doc.setTitle(article.getTitle());
                doc.setContent(article.getContent());
                articleSearchRepository.save(doc);
            } catch (Exception e) {
                log.warn("Elasticsearch index failed: {}", e.getMessage());
            }
        }
    }

    @CacheEvict(value = "articles", allEntries = true)
    public void delete(Integer id) {
        articleMapper.deleteById(id);
        evictInitCache();
        if (articleSearchRepository != null) {
            try {
                articleSearchRepository.deleteById(id);
            } catch (Exception e) {
                log.warn("Elasticsearch delete failed: {}", e.getMessage());
            }
        }
    }

    public List<Article> searchByKeyword(String keyword) {
        return articleMapper.searchByKeyword(keyword);
    }
}
