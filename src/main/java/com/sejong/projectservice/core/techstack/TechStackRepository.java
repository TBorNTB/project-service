package com.sejong.projectservice.core.techstack;

import org.springframework.stereotype.Repository;

@Repository
public interface TechStackRepository {
    TechStack  findOrCreateByName(String name);
}
