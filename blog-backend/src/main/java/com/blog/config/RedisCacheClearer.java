package com.blog.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * 应用启动时自动清理 Redis 中旧的 JDK 序列化数据，
 * 避免与当前 JSON 序列化器不兼容导致 Jackson 解析异常。
 */
@Component
@Order(1)
public class RedisCacheClearer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(RedisCacheClearer.class);

    private final RedisTemplate<String, Object> redisTemplate;

    public RedisCacheClearer(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void run(String... args) {
        try {
            redisTemplate.getConnectionFactory()
                    .getConnection()
                    .serverCommands()
                    .flushDb();
            log.info("✓ Redis 缓存已清理（旧序列化数据已清除）");
        } catch (Exception e) {
            log.warn("✗ Redis 缓存清理失败（可能 Redis 不可用或已为空）: {}", e.getMessage());
        }
    }
}
