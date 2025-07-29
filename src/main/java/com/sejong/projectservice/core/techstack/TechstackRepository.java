package com.sejong.projectservice.core.techstack;

public interface TechstackRepository {

    TechStack save(TechStack techStack);

    TechStack findById(Long techStackId);

    void deleteById(Long techStackId);
}
