package com.sejong.projectservice.domains.techstack.repository;

import com.sejong.projectservice.domains.techstack.domain.TechStack;

public interface TechstackRepository {

    TechStack save(TechStack techStack);

    TechStack findById(Long techStackId);

    void deleteById(Long techStackId);
}
