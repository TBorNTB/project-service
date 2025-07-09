package com.sejong.projectservice.core.yorkie;

import org.springframework.stereotype.Repository;

@Repository
public interface YorkieRepository {
    Yorkie save(Yorkie yorkie);

    Long findByProjectId(Long projectId);
}
