package com.sejong.projectservice.infrastructure.redis;

import com.sejong.projectservice.core.techstack.TechStack;
import com.sejong.projectservice.core.techstack.TechStackRepository;
import com.sejong.projectservice.infrastructure.techstack.entity.TechStackEntity;
import com.sejong.projectservice.infrastructure.techstack.repository.TechStackJpaRepository;
import com.sejong.projectservice.infrastructure.techstack.repository.TechStackRepositoryImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TechStackCacheRefresher {



}