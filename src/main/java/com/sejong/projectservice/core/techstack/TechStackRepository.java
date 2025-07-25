package com.sejong.projectservice.core.techstack;

public interface TechStackRepository {
    TechStack  findOrCreateByName(String name);
}
