package com.sejong.projectservice.core.yorkie;

public interface YorkieRepository {
    Yorkie save(Yorkie yorkie);

    Long findByProjectId(Long projectId);
}
