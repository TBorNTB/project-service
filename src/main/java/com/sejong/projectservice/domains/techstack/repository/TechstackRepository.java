package com.sejong.projectservice.domains.techstack.repository;

import com.sejong.projectservice.domains.techstack.domain.TechStackDto;

public interface TechstackRepository {

    TechStackDto save(TechStackDto techStackDto);

    TechStackDto findById(Long techStackId);

    void deleteById(Long techStackId);
}
