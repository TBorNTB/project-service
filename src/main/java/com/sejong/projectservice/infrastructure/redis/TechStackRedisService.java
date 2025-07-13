package com.sejong.projectservice.infrastructure.redis;

import com.sejong.projectservice.core.techstack.TechStack;
import com.sejong.projectservice.infrastructure.techstack.entity.TechStackEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class TechStackRedisService {
    private final RedisTemplate<String, String> redisTemplate;

    private String key(String techStackName) {
        return "techstack:" + techStackName;
    }

    public Optional<Long> getTechStackId(String techStackName) {
        String value = redisTemplate.opsForValue().get(key(techStackName));
        if (value == null) return Optional.empty();
        return Optional.of(Long.valueOf(value));
    }

    public void cacheTechStack(String techStackName, TechStack techStack) {
        redisTemplate.opsForValue().set(key(techStackName), String.valueOf(techStack));
    }

}